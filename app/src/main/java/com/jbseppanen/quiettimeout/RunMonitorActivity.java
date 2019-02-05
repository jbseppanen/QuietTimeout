package com.jbseppanen.quiettimeout;

import android.content.Intent;
import android.media.MediaRecorder;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_monitor);

        mProgressBar = findViewById(R.id.progress_level);

        Intent intent = getIntent();
        final Monitor monitor = (Monitor) intent.getSerializableExtra(MONITOR_KEY);

        countDownTimer = new CountDownTimer(monitor.getDuration(), 1000) {
            @Override
            public void onTick(final long millisUntilFinished) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.text_run_timer_display)).setText(String.valueOf(millisUntilFinished / 1000));
                    }
                });
            }

            @Override
            public void onFinish() {
                soundThread.interrupt();
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
            }
        }.start();

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
