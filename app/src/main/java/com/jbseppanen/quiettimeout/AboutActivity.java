package com.jbseppanen.quiettimeout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AboutActivity extends AppCompatActivity {

    public static final String LAYOUT_KEY = "layout_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = getIntent().getIntExtra(LAYOUT_KEY, R.layout.activity_about);
        setContentView(layoutId);
    }
}
