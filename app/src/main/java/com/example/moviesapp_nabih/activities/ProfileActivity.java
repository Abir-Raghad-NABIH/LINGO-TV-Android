package com.example.moviesapp_nabih.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.moviesapp_nabih.FavoritesManager;
import com.example.moviesapp_nabih.R;

public class ProfileActivity extends AppCompatActivity {

    TextView tvUsername, tvEmail, tvFavCount;
    FavoritesManager favoritesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvFavCount = findViewById(R.id.tvFavCount);

        favoritesManager = new FavoritesManager(this);

        // Récupérer les infos utilisateur
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String email = prefs.getString("user_email", "utilisateur@lingo.tv");
        String username = email.split("@")[0];

        tvEmail.setText(email);
        tvUsername.setText(username);
        tvFavCount.setText(String.valueOf(favoritesManager.getFavorites().size()));

        // Bouton retour
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Bouton déconnexion
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Bouton favoris
        findViewById(R.id.btnGoFavorites).setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritesActivity.class));
        });
    }
}