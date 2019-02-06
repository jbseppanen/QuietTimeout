package com.jbseppanen.quiettimeout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class MonitorRemoteActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ConnectionHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_remote);

        progressBar = findViewById(R.id.progress_remote_sound_level);
        helper = new ConnectionHelper();
        helper.discoverServices();

        helper.startReceiver(new ConnectionHelper.ReceiverCallback() {
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
    }

    @Override
    protected void onPause() {
        helper.shutdownServices();
        super.onPause();
    }

}
