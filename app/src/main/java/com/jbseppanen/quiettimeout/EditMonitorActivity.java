package com.jbseppanen.quiettimeout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class EditMonitorActivity extends AppCompatActivity {

    public static final String EDIT_MONITOR_KEY = "Monitor to edit";

    Monitor monitor;
    Context context;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.edit_navigation_run:
                    if (monitor.getId() == Monitor.NO_ID) {
                        MainActivity.viewModel.addMonitor(monitor);
                    } else {
                        MainActivity.viewModel.updateMonitor(monitor);
                    }
                    Intent intent = new Intent(MainActivity.context, RunMonitorActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.edit_navigation_save:
                    if (monitor.getId() == Monitor.NO_ID) {
                        MainActivity.viewModel.addMonitor(monitor);
                    } else {
                        MainActivity.viewModel.updateMonitor(monitor);
                    }
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
        final Monitor monitor = (Monitor) intent.getSerializableExtra(EDIT_MONITOR_KEY);
    }

}
