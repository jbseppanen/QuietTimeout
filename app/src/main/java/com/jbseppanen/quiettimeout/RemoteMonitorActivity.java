package com.jbseppanen.quiettimeout;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class RemoteMonitorActivity extends AppCompatActivity {
    static final String SOUND_LEVEL_SERVICE_NAME = "QuietTimeout_sound_level_service";
    static final String MONITOR_INFO_SERVICE_NAME = "QuietTimeout_monitor_info_service";

    private ProgressBar progressBar;
    private SeekBar seekBar;
    private TextView textView;
    private ConnectionHelper soundLevelHelper;
    private ConnectionHelper monitorInfoHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_remote);


        progressBar = findViewById(R.id.progress_remote_sound_level);
        seekBar = findViewById(R.id.seekbar_remote_threshold);
        seekBar.setEnabled(false);
        textView = findViewById(R.id.text_remote_duration);


        soundLevelHelper = new ConnectionHelper(SOUND_LEVEL_SERVICE_NAME);
        soundLevelHelper.discoverServices();

        soundLevelHelper.startReceiver(new ConnectionHelper.ReceiverCallback() {
            @Override
            public void returnResult(final String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int progress = Integer.parseInt(result);
                        progressBar.setProgress(progress);
                    }
                });
            }
        });

        monitorInfoHelper = new ConnectionHelper(MONITOR_INFO_SERVICE_NAME);
        monitorInfoHelper.discoverServices();
        monitorInfoHelper.startReceiver(new ConnectionHelper.ReceiverCallback() {
            @Override
            public void returnResult(String result) {
                Log.i(monitorInfoHelper.TAG, "Monitor Info: " + result);
                final String[] strings = result.split(":");
                if (strings.length >= 3) {

                    final String displayValue;
                    int millisUntilFinished = Integer.parseInt(strings[2]);
                    if (millisUntilFinished > 60000) {
                        displayValue = String.format("%02d:%02d:%02d", millisUntilFinished / 3600000, ((millisUntilFinished % 3600000) / 60000), ((millisUntilFinished % 60000) / 1000));
                    } else {
                        displayValue = String.valueOf(millisUntilFinished / 1000);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(displayValue);
                            seekBar.setProgress(Integer.parseInt(strings[1]));
                        }
                    });
                }
            }
        });


    }

    @Override
    protected void onPause() {
        soundLevelHelper.shutdownServices();
        monitorInfoHelper.shutdownServices();
        super.onPause();
    }

}
