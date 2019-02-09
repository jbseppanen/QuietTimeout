package com.jbseppanen.quiettimeout;

import android.Manifest;
import android.app.Activity;
import android.app.UiModeManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final int RECORD_REQUEST_CODE = 1;

    private Toolbar toolbar;
    static Context context;
    private DrawerLayout drawerLayout;
    private GridLayoutManager layoutManager;
    private RecyclerView listView;
    private MonitorListAdapter listAdapter;
    static MonitorViewModel viewModel;
    boolean firstTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_REQUEST_CODE);
            firstTime = true;
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                menuItem.setChecked(!menuItem.isChecked());

                switch (menuItem.getItemId()) {
                    case R.id.nav_night_mode:
                        return false;
                    case R.id.nav_settings:
                        Intent intent = new Intent(context, SettingsActivity.class);
                        startActivity(intent);
                        break;

                }

                if (menuItem.getItemId() == R.id.nav_night_mode) {
                    return false;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        listView = findViewById(R.id.monitor_recycler_view);

        layoutManager = new GridLayoutManager(context, 2);
        listView.setLayoutManager(layoutManager);

        viewModel = ViewModelProviders.of(this).get(MonitorViewModel.class);


        final Observer<ArrayList<Monitor>> observer = new Observer<ArrayList<Monitor>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Monitor> monitors) {
                if (monitors != null) {
                    if (listAdapter == null) {
                        listAdapter = new MonitorListAdapter(monitors, (Activity) context);
                        listView.setAdapter(listAdapter);
                    } else {
                        listAdapter.replaceList(monitors);
                        listAdapter.notifyDataSetChanged();
                    }
                }
            }
        };
        viewModel.getNotesList().observe(this, observer);

        if (firstTime) {
           findViewById(R.id.help_view).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_REQUEST_CODE) {
            if (!(permissions[0].equals(Manifest.permission.RECORD_AUDIO) && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(context, "Need to grant permission to use recording.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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
            case R.id.options_view_help:
                Intent helpIntent = new Intent(context, AboutActivity.class);
                int layoutId = R.layout.help_layout;
                helpIntent.putExtra(AboutActivity.LAYOUT_KEY, layoutId);
                startActivity(helpIntent);
                break;
            case R.id.options_add_monitor:
                findViewById(R.id.help_view).setVisibility(View.GONE);
                Intent intent = new Intent(context, EditMonitorActivity.class);
                intent.putExtra(EditMonitorActivity.EDIT_MONITOR_KEY, new Monitor());
                startActivity(intent);
                break;

            case R.id.options_monitor_remote:
                Intent remoteIntent = new Intent(context, RemoteMonitorActivity.class);
                startActivity(remoteIntent);
                break;

        }
        return true;
    }

}
