package com.jbseppanen.quiettimeout;

import android.content.Intent;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jbseppanen.quiettimeout.views.TimerView;

import java.io.IOException;


public class RunMonitorActivity extends AppCompatActivity {

    public static final String RUN_MONITOR_KEY = "Monitor to Run";

    private MediaRecorder recorder;
    private Thread soundThread;
    private Thread infoThread;
    private ProgressBar mProgressBar;
    private CountDownTimer countDownTimer;
    private long timeLeft;
    TextView timerDisplay;
    TimerView timerView;
    private ConnectionHelper soundLevelHelper;
    private ConnectionHelper monitorInfoHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_monitor);

        mProgressBar = findViewById(R.id.progress_run_sound_level);

        timerDisplay = findViewById(R.id.text_run_timer_display);
        timerView = findViewById(R.id.timer_view);

        Intent intent = getIntent();
        final Monitor monitor = (Monitor) intent.getSerializableExtra(RUN_MONITOR_KEY);

        monitorInfoHelper = new ConnectionHelper(RemoteMonitorActivity.MONITOR_INFO_SERVICE_NAME);
        monitorInfoHelper.registerService();
        monitorInfoHelper.send(monitor.toString());

        mProgressBar.setSecondaryProgress(monitor.getThreshold());

        countDownTimer = new CountDownTimer(monitor.getDuration(), 1000) {
            @Override
            public void onTick(final long millisUntilFinished) {
                String displayValue;
                if (millisUntilFinished > 60000) {
                    displayValue = String.format("%02d:%02d:%02d", (int) millisUntilFinished / 3600000, (int) ((millisUntilFinished % 3600000) / 60000), (int) ((millisUntilFinished % 60000) / 1000));
                } else {
                    displayValue = String.valueOf(millisUntilFinished / 1000);
                }
                timerDisplay.setText(displayValue);
                float level = millisUntilFinished / (float) monitor.getDuration();
                timerView.updateLevel(level);
                timeLeft = millisUntilFinished;
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

        soundLevelHelper = new ConnectionHelper(RemoteMonitorActivity.SOUND_LEVEL_SERVICE_NAME);
        soundLevelHelper.registerService();

        soundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long lastSentTime = 0;
                long lastProgressUpdate = 0;
                String messageToSend;
                while (soundThread != null && !soundThread.isInterrupted()) {
                    try {
                        if (recorder != null) {
                            int maxAmplitude = recorder.getMaxAmplitude();
                            if (maxAmplitude > 0) {
                                mProgressBar.setProgress(maxAmplitude);
                                if (((Math.abs(lastProgressUpdate - maxAmplitude) > 100) || (Math.abs(lastSentTime - timeLeft) > 5000))) {
                                    messageToSend = maxAmplitude + ":" + monitor.getThreshold() + ":" + timeLeft;
                                    soundLevelHelper.send(messageToSend);
//                                    soundLevelHelper.send(String.valueOf(maxAmplitude));
                                    lastSentTime = timeLeft;
                                    lastProgressUpdate = maxAmplitude;
                                }
                                if (maxAmplitude > monitor.getThreshold()) {
                                    countDownTimer.cancel();
                                    recorder.stop();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            timerDisplay.setText("Too Loud!");
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

        infoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (infoThread != null && !infoThread.isInterrupted()) {
                    Monitor infoMonitor = new Monitor(monitor.NO_ID, monitor.getThreshold(), (int) timeLeft);
                    monitorInfoHelper.send(infoMonitor.toString());
                    //send update every 15 seconds.
                    try {
                        infoThread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
//        infoThread.start();


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
        soundLevelHelper.shutdownServices();
//        monitorInfoHelper.shutdownServices();
        soundThread.interrupt();
        soundThread = null;
        countDownTimer.cancel();
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        super.onPause();
    }
}
