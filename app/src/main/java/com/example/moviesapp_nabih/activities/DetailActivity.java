package com.example.moviesapp_nabih.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.moviesapp_nabih.FavoritesManager;
import com.example.moviesapp_nabih.R;
import com.example.moviesapp_nabih.models.Movie;

public class DetailActivity extends AppCompatActivity {

    ImageView ivBackdrop, ivPoster;
    TextView tvTitle, tvOverview, tvRating, tvDate;
    Button btnFavorite;
    FavoritesManager favoritesManager;
    Movie currentMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ivBackdrop = findViewById(R.id.ivBackdrop);
        ivPoster = findViewById(R.id.ivPoster);
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        tvRating = findViewById(R.id.tvRating);
        tvDate = findViewById(R.id.tvDate);
        btnFavorite = findViewById(R.id.btnFavorite);

        favoritesManager = new FavoritesManager(this);

        // Récupérer les données
        currentMovie = new Movie();
        currentMovie.setId(getIntent().getIntExtra("movie_id", 0));
        currentMovie.setTitle(getIntent().getStringExtra("movie_title"));
        currentMovie.setOverview(getIntent().getStringExtra("movie_overview"));
        currentMovie.setPosterPath(getIntent().getStringExtra("movie_poster"));
        currentMovie.setBackdropPath(getIntent().getStringExtra("movie_backdrop"));
        currentMovie.setVoteAverage(getIntent().getDoubleExtra("movie_rating", 0));
        currentMovie.setReleaseDate(getIntent().getStringExtra("movie_date"));

        tvTitle.setText(currentMovie.getTitle());
        tvOverview.setText(currentMovie.getOverview());
        tvRating.setText("⭐ Note : " + currentMovie.getVoteAverage());
        tvDate.setText("📅 Date : " + currentMovie.getReleaseDate());

        Glide.with(this)
                .load(getIntent().getStringExtra("movie_backdrop"))
                .into(ivBackdrop);

        Glide.with(this)
                .load(getIntent().getStringExtra("movie_poster"))
                .into(ivPoster);

        // Bouton favori
        updateFavoriteButton();

        btnFavorite.setOnClickListener(v -> {
            if (favoritesManager.isFavorite(currentMovie)) {
                favoritesManager.removeFavorite(currentMovie);
                Toast.makeText(this, "Retiré des favoris", Toast.LENGTH_SHORT).show();
            } else {
                favoritesManager.addFavorite(currentMovie);
                Toast.makeText(this, "Ajouté aux favoris ❤️", Toast.LENGTH_SHORT).show();
            }
            updateFavoriteButton();
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void updateFavoriteButton() {
        if (favoritesManager.isFavorite(currentMovie)) {
            btnFavorite.setText("❤️ Retiré des favoris");
            btnFavorite.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#888888")));
        } else {
            btnFavorite.setText("❤️ Ajouter aux favoris");
            btnFavorite.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#e94560")));
        }
    }
}