package com.example.moviesapp_nabih.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.moviesapp_nabih.R;
import com.example.moviesapp_nabih.adapters.CardMovieAdapter;
import com.example.moviesapp_nabih.api.ApiService;
import com.example.moviesapp_nabih.api.MovieResponse;
import com.example.moviesapp_nabih.api.RetrofitClient;
import com.example.moviesapp_nabih.models.Movie;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.view.View;
import android.net.Uri;
import com.example.moviesapp_nabih.FavoritesManager;

public class HomeActivity extends AppCompatActivity {

    ImageView ivBanner;
    TextView tvBannerTitle;
    RecyclerView rvPopular, rvTopRated, rvAction, rvComedy;
    CardMovieAdapter popularAdapter, topRatedAdapter, actionAdapter, comedyAdapter;

    List<Movie> popularMovies = new ArrayList<>();
    List<Movie> topRatedMovies = new ArrayList<>();
    List<Movie> actionMovies = new ArrayList<>();
    List<Movie> comedyMovies = new ArrayList<>();

    TextView tvFavBadge;
    FavoritesManager favoritesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ivBanner = findViewById(R.id.ivBanner);
        tvBannerTitle = findViewById(R.id.tvBannerTitle);
        rvPopular = findViewById(R.id.rvPopular);
        rvTopRated = findViewById(R.id.rvTopRated);
        rvAction = findViewById(R.id.rvAction);
        rvComedy = findViewById(R.id.rvComedy);
        tvFavBadge = findViewById(R.id.tvFavBadge);
        favoritesManager = new FavoritesManager(this);


        // Setup adapters
        popularAdapter = new CardMovieAdapter(this, popularMovies, movie -> openDetail(movie));
        topRatedAdapter = new CardMovieAdapter(this, topRatedMovies, movie -> openDetail(movie));
        actionAdapter = new CardMovieAdapter(this, actionMovies, movie -> openDetail(movie));
        comedyAdapter = new CardMovieAdapter(this, comedyMovies, movie -> openDetail(movie));

        rvPopular.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTopRated.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvAction.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvComedy.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rvPopular.setAdapter(popularAdapter);
        rvTopRated.setAdapter(topRatedAdapter);
        rvAction.setAdapter(actionAdapter);
        rvComedy.setAdapter(comedyAdapter);

        // Navigation
        findViewById(R.id.navSearch).setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
        });
        findViewById(R.id.navFavorites).setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritesActivity.class));
        });
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, AvatarActivity.class));
        });

        // Charger les films
        loadPopularMovies();
        loadTopRatedMovies();
        loadGenreMovies(28, actionAdapter, actionMovies);   // Action
        loadGenreMovies(35, comedyAdapter, comedyMovies);   // Comédie

        // Bouton info
        findViewById(R.id.btnInfo).setOnClickListener(v -> {
            if (!popularMovies.isEmpty()) {
                openDetail(popularMovies.get(0));
            }
        });

        findViewById(R.id.btnPlay).setOnClickListener(v -> {
            if (!popularMovies.isEmpty()) {
                Movie featured = popularMovies.get(0);
                String searchQuery = featured.getTitle() + " bande annonce officielle";
                Uri uri = Uri.parse("https://www.youtube.com/results?search_query=" +
                        Uri.encode(searchQuery));
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
        findViewById(R.id.navMaps).setOnClickListener(v -> {
            startActivity(new Intent(this, MapsActivity.class));
        });
        findViewById(R.id.navAI).setOnClickListener(v -> {
            startActivity(new Intent(this, ChatAIActivity.class));
        });
    }

    private void loadPopularMovies() {
        RetrofitClient.getInstance().getApiService()
                .getPopularMovies(ApiService.API_KEY, "fr-FR", 1)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            popularAdapter.updateMovies(movies);
                            if (!movies.isEmpty()) {
                                Movie featured = movies.get(0);
                                tvBannerTitle.setText(featured.getTitle());
                                Glide.with(HomeActivity.this)
                                        .load(featured.getFullBackdropPath())
                                        .into(ivBanner);
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Toast.makeText(HomeActivity.this, "Erreur connexion", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadTopRatedMovies() {
        RetrofitClient.getInstance().getApiService()
                .getTopRatedMovies(ApiService.API_KEY, "fr-FR", 1)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            topRatedAdapter.updateMovies(response.body().getResults());
                        }
                    }
                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {}
                });
    }

    private void loadGenreMovies(int genreId, CardMovieAdapter adapter, List<Movie> list) {
        RetrofitClient.getInstance().getApiService()
                .getMoviesByGenre(ApiService.API_KEY, "fr-FR", 1, genreId)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            adapter.updateMovies(response.body().getResults());
                        }
                    }
                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {}
                });
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
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    private void updateFavBadge() {
        int count = favoritesManager.getFavorites().size();
        if (count > 0) {
            tvFavBadge.setVisibility(View.VISIBLE);
            tvFavBadge.setText(String.valueOf(count));
        } else {
            tvFavBadge.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFavBadge();
    }
}