package com.example.moviesapp_nabih.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.moviesapp_nabih.R;

public class MainActivity extends AppCompatActivity {

    TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tvWelcome);

        String email = getIntent().getStringExtra("email");
        if (email != null) {
            tvWelcome.setText("Bienvenue, " + email + " !");
        }
    }
}