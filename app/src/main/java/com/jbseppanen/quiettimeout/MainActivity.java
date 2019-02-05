package com.jbseppanen.quiettimeout;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    Context context;
    DrawerLayout drawerLayout;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = findViewById(R.id.text_view_main);
        textView.setText("Main Activity");

        drawerLayout = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        MenuItem item = navigationView.getMenu().findItem(R.id.nav_night_mode);
        View rootView = item.getActionView();
        SwitchCompat themeSwitch = rootView.findViewById(R.id.drawer_switch);
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    final UiModeManager uiModeManager = context.getSystemService(UiModeManager.class);
                    uiModeManager.setNightMode(isChecked ? UiModeManager.MODE_NIGHT_YES : UiModeManager.MODE_NIGHT_NO);
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {
                Toast.makeText(context, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                menuItem.setChecked(!menuItem.isChecked());

                switch (menuItem.getItemId()) {
                    case R.id.nav_night_mode:
                        return false;
                    case R.id.nav_settings:
                        textView.setText("Settings Activity");
                        break;

                }

                if (menuItem.getItemId() == R.id.nav_night_mode) {
                    return false;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });



        Monitor monitor = new Monitor(10, 30000);

        Intent intent = new Intent(context, RunMonitorActivity.class);
        intent.putExtra(RunMonitorActivity.MONITOR_KEY, monitor);
        startActivity(intent);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.options_view_log:
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_LONG).show();
                textView.setText("View Log Activity");
                break;
            case R.id.options_add_config:
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_LONG).show();
                textView.setText("NoiseMonitorConfig activity");
                break;
        }
        return true;
    }
}
