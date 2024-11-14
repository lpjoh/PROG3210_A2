package com.example.prog3210_a2.adapters;

import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prog3210_a2.R;
import com.example.prog3210_a2.models.Movie;

public class MovieSearchAdapter extends RecyclerView.Adapter<MovieSearchAdapter.ViewHolder> {
    private final Movie[] movies;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText, studioText, ratingText;

        public ViewHolder(View view) {
            super(view);

            titleText = view.findViewById(R.id.titleText);
            studioText = view.findViewById(R.id.studioText);
            ratingText = view.findViewById(R.id.ratingText);
        }

        public TextView getTitleText() {
            return titleText;
        }

        public TextView getStudioText() {
            return studioText;
        }

        public TextView getRatingText() {
            return ratingText;
        }
    }

    public MovieSearchAdapter(Movie[] movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.movie_search_cell, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Movie movie = movies[position];

        viewHolder.getTitleText().setText(
                String.format("%s (%s)", movie.title, movie.year));

        viewHolder.getStudioText().setText(movie.director);
        viewHolder.getRatingText().setText(movie.rating);

        // Tag view to movie index
        viewHolder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return movies.length;
    }
}