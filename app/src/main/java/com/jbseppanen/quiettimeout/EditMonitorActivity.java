package com.jbseppanen.quiettimeout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;

public class EditMonitorActivity extends AppCompatActivity {

    public static final String EDIT_MONITOR_KEY = "Monitor to edit";

    Monitor monitor;
    EditText editViewDuration;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.edit_navigation_run:
                    updateMonitor();
                    Intent intent = new Intent(MainActivity.context, RunMonitorActivity.class);
                    intent.putExtra(RunMonitorActivity.RUN_MONITOR_KEY, monitor);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.edit_navigation_save:
                    updateMonitor();
                    finish();
                    return true;
                case R.id.edit_navigation_delete:
                    if (monitor.getId() != Monitor.NO_ID) {
                        MainActivity.viewModel.deleteMonitor(monitor);
                    }
                    finish();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_monitor);

        ((BottomNavigationView) findViewById(R.id.navigation)).setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Intent intent = getIntent();
        monitor = (Monitor) intent.getSerializableExtra(EDIT_MONITOR_KEY);

        editViewDuration = findViewById(R.id.edit_duration);
        String displayValue = String.format("%d:%02d", monitor.getDuration() / 60000, (monitor.getDuration() % 60000) / 1000);
        editViewDuration.setText(displayValue);
    }

    void updateMonitor() {
        String[] times = editViewDuration.getText().toString().split(":");
        int totalMs = 0;
        int msMultiplier = 1000;
        for (String time : times) {
            totalMs += Integer.parseInt(time) * msMultiplier;
            msMultiplier *= 60;
        }
        monitor.setDuration(totalMs);
        if (monitor.getId() == Monitor.NO_ID) {
            MainActivity.viewModel.addMonitor(monitor);
        } else {
            MainActivity.viewModel.updateMonitor(monitor);
        }
    }
}
