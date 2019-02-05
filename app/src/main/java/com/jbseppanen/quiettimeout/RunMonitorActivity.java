package com.jbseppanen.quiettimeout;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class RunMonitorActivity extends AppCompatActivity {

    public static final String MONITOR_KEY = "Monitor to Run";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_monitor);

        Intent intent = getIntent();
        Monitor monitor = (Monitor) intent.getSerializableExtra(MONITOR_KEY);

        CountDownTimer countDownTimer = new CountDownTimer(monitor.getDuration(), 1000) {
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

            }
        }.start();

    }
}
