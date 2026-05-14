package com.example.moviesapp_nabih;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.moviesapp_nabih.models.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoritesManager {

    private static final String PREFS_NAME = "favorites_prefs";
    private static final String KEY_FAVORITES = "favorites_list";

    private SharedPreferences prefs;
    private Gson gson;

    public FavoritesManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public List<Movie> getFavorites() {
        String json = prefs.getString(KEY_FAVORITES, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Movie>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void addFavorite(Movie movie) {
        List<Movie> favorites = getFavorites();
        for (Movie m : favorites) {
            if (m.getId() == movie.getId()) return;
        }
        favorites.add(movie);
        saveFavorites(favorites);
    }

    public void removeFavorite(Movie movie) {
        List<Movie> favorites = getFavorites();
        favorites.removeIf(m -> m.getId() == movie.getId());
        saveFavorites(favorites);
    }

    public boolean isFavorite(Movie movie) {
        for (Movie m : getFavorites()) {
            if (m.getId() == movie.getId()) return true;
        }
        return false;
    }

    private void saveFavorites(List<Movie> favorites) {
        prefs.edit().putString(KEY_FAVORITES, gson.toJson(favorites)).apply();
    }
}