package com.jbseppanen.quiettimeout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    public static final String LAYOUT_KEY = "layout_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = getIntent().getIntExtra(LAYOUT_KEY, R.layout.activity_about);
        setContentView(layoutId);
    }
}
