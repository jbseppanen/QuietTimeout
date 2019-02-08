package com.jbseppanen.quiettimeout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.nsd.NsdServiceInfo;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class RemoteMonitorActivity extends AppCompatActivity {
    static final String SOUND_LEVEL_SERVICE_NAME = "QuietTimeout_sound_level_service";

    private ProgressBar progressBar;
    private SeekBar seekBar;
    private TextView textView;
    private CountDownTimer countDownTimer;
    private ConnectionHelper soundLevelHelper;
    private long timeLeft;
    boolean notify;
    Ringtone ringtone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_remote);

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);

        notify = sharedPref.getBoolean("notifications_play_sound", true);
        String ringtonePath = sharedPref.getString("notifications_new_message_ringtone", "content://settings/system/notification_sound");
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(ringtonePath));


        progressBar = findViewById(R.id.progress_remote_sound_level);
        seekBar = findViewById(R.id.seekbar_remote_threshold);
        seekBar.setEnabled(false);
        textView = findViewById(R.id.text_remote_duration);

        soundLevelHelper = new ConnectionHelper(SOUND_LEVEL_SERVICE_NAME);
        soundLevelHelper.discoverServices(new ConnectionHelper.ConnectionCallback() {
            @Override
            public void returnResult(NsdServiceInfo result) {
                Messenger messenger = new Messenger(new IncomingHandler());
                Intent intent = new Intent(getApplicationContext(), ReceivingService.class);
                intent.putExtra(ReceivingService.MESSENGER_KEY, messenger);
                intent.putExtra(ReceivingService.SERVICE_INFO_KEY, result);
                if (result != null) {
                    startService(intent);
                } else {
                    stopService(intent);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(getString(R.string.lost_connection));
                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        soundLevelHelper.shutdownServices();
        Intent intent = new Intent(getApplicationContext(), ReceivingService.class);
        intent.putExtra(ReceivingService.MESSENGER_KEY, (Parcelable[]) null);
        startService(intent);
        super.onStop();
    }

    void updateCountdownTimer(final long time) {
        countDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String displayValue;
                if (millisUntilFinished > 60000) {
                    displayValue = String.format("%02d:%02d:%02d", (int) millisUntilFinished / 3600000, (int) ((millisUntilFinished % 3600000) / 60000), (int) ((millisUntilFinished % 60000) / 1000));
                } else {
                    displayValue = String.valueOf(millisUntilFinished / 1000);
                }
                textView.setText(displayValue);
                timeLeft = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                textView.setText("DONE!");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ringtone.play();
                    }
                }).start();
            }
        };
        countDownTimer.start();
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String result = (String) bundle.get(ReceivingService.MESSAGE_KEY);
            final String[] strings = result.split(":");
            if (strings.length >= 3) {
                final int progress = Integer.parseInt(strings[0]);
                final long millisUntilFinished = Long.parseLong(strings[2]);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(progress);
                        seekBar.setProgress(Integer.parseInt(strings[1]));
                        if (countDownTimer == null) {
                            updateCountdownTimer(millisUntilFinished);
                        } else if ((Math.abs(timeLeft - millisUntilFinished) > 2000)) {
                            countDownTimer.cancel();
                            updateCountdownTimer(millisUntilFinished);
                        }
                    }
                });
            }
        }
    }
}
