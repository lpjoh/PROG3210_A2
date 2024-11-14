package com.example.prog3210_a2;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private final Context context;
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

    public MoviesAdapter(Context context, Movie[] movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.movie_cell, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Movie movie = movies[position];

        viewHolder.getTitleText().setText(
                String.format("%s (%s)", movie.getTitle(), movie.getYear()));
        viewHolder.getStudioText().setText(movie.getStudio());

        String ratingPrefix = context.getResources().getString(R.string.movie_rating_prefix);
        viewHolder.getRatingText().setText(
                String.format("%s %s", ratingPrefix, movie.getRating()));
    }

    @Override
    public int getItemCount() {
        return movies.length;
    }
}