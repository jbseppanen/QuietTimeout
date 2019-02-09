package com.jbseppanen.quiettimeout;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jbseppanen.quiettimeout.views.TimerView;

import java.io.IOException;

import static java.lang.Thread.sleep;


public class RunMonitorActivity extends AppCompatActivity {

    public static final String RUN_MONITOR_KEY = "Monitor to Run";

    private MediaRecorder recorder;
    private Thread soundThread;
    private ProgressBar mProgressBar;
    private SeekBar seekBar;
    private CountDownTimer countDownTimer;
    private long timeLeft;
    TextView timerDisplay;
    TimerView timerView;
    private ConnectionHelper helper;
    private ImageView imageView;
    boolean notify;
    Ringtone ringtone;
    boolean allowRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_monitor);

//        startLockTask();

        Intent intent = getIntent();
        final Monitor monitor = (Monitor) intent.getSerializableExtra(RUN_MONITOR_KEY);

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);

        notify = sharedPref.getBoolean("notifications_play_sound", true);
        String ringtonePath = sharedPref.getString("notifications_new_message_ringtone", "content://settings/system/notification_sound");
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(ringtonePath));
        allowRemote = sharedPref.getBoolean("sync_remote", true);

        mProgressBar = findViewById(R.id.progress_run_sound_level);
        seekBar = findViewById(R.id.seekbar_run_threshold);
        seekBar.setEnabled(false);
        seekBar.setProgress(monitor.getThreshold());

        timerDisplay = findViewById(R.id.text_run_timer_display);
        timerView = findViewById(R.id.timer_view);

        imageView = findViewById(R.id.image_run_complete);

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
                if (notify) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ringtone.play();
                            try {
                                sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ringtone.stop();
                        }
                    }).start();
                }

                soundThread.interrupt();
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
                timerView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.animate()
                                .alpha(1f)
                                .scaleX(15)
                                .scaleY(15)
                                .translationZ(10f)
                                .setInterpolator(new OvershootInterpolator(10))
                                .setStartDelay(200)
                                .setDuration(1000)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {
                                    }
                                })
                                .start();
                    }
                });
            }
        }.start();

        initializeRecorder();
        recorder.start();
        if (allowRemote) {
            helper = new ConnectionHelper(RemoteMonitorActivity.SOUND_LEVEL_SERVICE_NAME);
            helper.registerService();
        }

        soundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long lastSentTime = 0;
                long lastProgressUpdate = 0;
                String messageToSend;
                while (soundThread != null) {
                    if (soundThread.isInterrupted()) {
                        break;
                    }
                    try {
                        if (recorder != null) {
                            int maxAmplitude = recorder.getMaxAmplitude();
                            if (maxAmplitude > 0) {
                                mProgressBar.setProgress(maxAmplitude);
                                if (((Math.abs(lastProgressUpdate - maxAmplitude) > 100) || (Math.abs(lastSentTime - timeLeft) > 5000))) {
                                    messageToSend = maxAmplitude + ":" + monitor.getThreshold() + ":" + timeLeft;
                                    if (allowRemote) {
                                        helper.send(messageToSend);
                                    }
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
                                    if (notify) {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        final Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ringtone.play();
                                            }
                                        }).start();
                                        sleep(2000);
                                        if (ringtone.isPlaying()) {
                                            ringtone.stop();
                                        }
                                        while (ringtone.isPlaying()) {
                                            sleep(1000);
                                        }
                                    }
                                    initializeRecorder();
                                    recorder.start();
                                    countDownTimer.start();
                                }
                            }
                            sleep(100);
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
        if (helper != null) {
            helper.shutdownServices();
        }
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