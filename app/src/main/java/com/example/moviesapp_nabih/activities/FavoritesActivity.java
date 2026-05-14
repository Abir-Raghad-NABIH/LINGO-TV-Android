package com.example.moviesapp_nabih.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moviesapp_nabih.FavoritesManager;
import com.example.moviesapp_nabih.R;
import com.example.moviesapp_nabih.adapters.CardMovieAdapter;
import com.example.moviesapp_nabih.models.Movie;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    RecyclerView rvFavorites;
    TextView tvEmpty;
    CardMovieAdapter adapter;
    FavoritesManager favoritesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        rvFavorites = findViewById(R.id.rvFavorites);
        tvEmpty = findViewById(R.id.tvEmpty);

        favoritesManager = new FavoritesManager(this);

        List<Movie> favorites = favoritesManager.getFavorites();

        if (favorites.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvFavorites.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvFavorites.setVisibility(View.VISIBLE);

            adapter = new CardMovieAdapter(this, favorites, movie -> openDetail(movie));
            rvFavorites.setLayoutManager(new GridLayoutManager(this, 3));
            rvFavorites.setAdapter(adapter);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void openDetail(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("movie_id", movie.getId());
        intent.putExtra("movie_title", movie.getTitle());
        intent.putExtra("movie_overview", movie.getOverview());
        intent.putExtra("movie_poster", movie.getFullPosterPath());
        intent.putExtra("movie_backdrop", movie.getFullBackdropPath());
        intent.putExtra("movie_rating", movie.getVoteAverage());
        intent.putExtra("movie_date", movie.getReleaseDate());
        startActivity(intent);
    }
}