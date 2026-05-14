package com.example.moviesapp_nabih.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class SearchActivity extends AppCompatActivity {

    EditText etSearch;
    ImageView btnSearch;
    RecyclerView rvSearch;

    CardMovieAdapter adapter;
    List<Movie> movieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        rvSearch = findViewById(R.id.rvSearch);

        adapter = new CardMovieAdapter(this, movieList, movie -> openDetail(movie));

        rvSearch.setLayoutManager(new GridLayoutManager(this, 3));
        rvSearch.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();

            if (!query.isEmpty()) {
                searchMovies(query);
            }
        });
    }

    private void searchMovies(String query) {

        RetrofitClient.getInstance().getApiService()
                .searchMovies(ApiService.API_KEY, query, "fr-FR")
                .enqueue(new Callback<MovieResponse>() {

                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            adapter.updateMovies(response.body().getResults());

                        } else {
                            Toast.makeText(SearchActivity.this, "Aucun résultat", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {

                        Toast.makeText(SearchActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
                    }
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
    }
}