package com.example.moviesapp_nabih.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moviesapp_nabih.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {

            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left);
            finish();

        }, 3000);
    }
}