package com.jbseppanen.quiettimeout;

import android.content.Intent;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;


public class RunMonitorActivity extends AppCompatActivity {

    public static final String MONITOR_KEY = "Monitor to Run";

    private MediaRecorder recorder;
    private Thread soundThread;
    private ProgressBar mProgressBar;
    private CountDownTimer countDownTimer;
    TextView timerDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_monitor);

        mProgressBar = findViewById(R.id.progress_level);
        timerDisplay = findViewById(R.id.text_run_timer_display);

        Intent intent = getIntent();
        final Monitor monitor = (Monitor) intent.getSerializableExtra(MONITOR_KEY);

        countDownTimer = new CountDownTimer(monitor.getDuration(), 1000) {
            @Override
            public void onTick(final long millisUntilFinished) {
                String displayValue;
                if (millisUntilFinished > 60000) {
                    displayValue = String.format("%d:%02d", (int) millisUntilFinished / 60000, (millisUntilFinished % 60000) / 1000);
                } else {
                    displayValue = String.valueOf(millisUntilFinished / 1000);
                }
                timerDisplay.setText(displayValue);
            }

            @Override
            public void onFinish() {
                timerDisplay.setText("DONE!");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        r.play();
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        r.stop();
                    }
                }).start();
                soundThread.interrupt();
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
            }
        }.start();

        initializeRecorder();
        recorder.start();

        soundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (soundThread != null && !soundThread.isInterrupted()) {
                    try {
                        if (recorder != null) {
                            int maxAmplitude = recorder.getMaxAmplitude();
                            if (maxAmplitude > 0) {
                                mProgressBar.setProgress(maxAmplitude);
                                if (maxAmplitude > monitor.getThreshold()) {
                                    countDownTimer.cancel();
                                    recorder.stop();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            timerDisplay.setText("RESET!");
                                        }
                                    });
                                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    final Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ringtone.play();
                                        }
                                    }).start();
                                    soundThread.sleep(2000);
                                    if (ringtone.isPlaying()) {
                                        ringtone.stop();
                                    }
                                    while (ringtone.isPlaying()) {
                                        soundThread.sleep(1000);
                                    }
                                    initializeRecorder();
                                    recorder.start();
                                    countDownTimer.start();
                                }
                            }
                            soundThread.sleep(100);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        soundThread.start();
    }

    private void initializeRecorder() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile("/dev/null");
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        soundThread.interrupt();
        soundThread = null;
        countDownTimer.cancel();
        if (recorder != null) {
            recorder.stop();
            recorder.release();
        }
        super.onPause();
    }
}