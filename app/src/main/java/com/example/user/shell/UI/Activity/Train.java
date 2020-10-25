package com.example.user.shell.UI.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.user.shell.UI.Activity.R;

public class Train extends AppCompatActivity {

    int cookie = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_train);
        cookie=getIntent().getIntExtra("cookie",1000);
    }
}
