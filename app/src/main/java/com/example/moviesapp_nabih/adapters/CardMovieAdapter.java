package com.example.moviesapp_nabih.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.moviesapp_nabih.R;
import com.example.moviesapp_nabih.models.Movie;
import java.util.List;

public class CardMovieAdapter extends RecyclerView.Adapter<CardMovieAdapter.CardViewHolder> {

    private Context context;
    private List<Movie> movies;
    private OnMovieClickListener listener;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public CardMovieAdapter(Context context, List<Movie> movies, OnMovieClickListener listener) {
        this.context = context;
        this.movies = movies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.tvTitle.setText(movie.getTitle());

        // Note colorée
        double rating = movie.getVoteAverage();
        String ratingText = String.format("⭐ %.1f", rating);
        holder.tvRating.setText(ratingText);

        if (rating >= 7.5) {
            holder.tvRating.setTextColor(android.graphics.Color.parseColor("#00FF00"));
        } else if (rating >= 5.0) {
            holder.tvRating.setTextColor(android.graphics.Color.parseColor("#FFA500"));
        } else {
            holder.tvRating.setTextColor(android.graphics.Color.parseColor("#FF0000"));
        }

        // Année
        String date = movie.getReleaseDate();
        if (date != null && date.length() >= 4) {
            holder.tvYear.setText(date.substring(0, 4));
        }

        Glide.with(context)
                .load(movie.getFullPosterPath())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivPoster);

        holder.itemView.setOnClickListener(v -> listener.onMovieClick(movie));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void updateMovies(List<Movie> newMovies) {
        movies.clear();
        movies.addAll(newMovies);
        notifyDataSetChanged();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle, tvRating, tvYear;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvYear = itemView.findViewById(R.id.tvYear);
        }
    }
}