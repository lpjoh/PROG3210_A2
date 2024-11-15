package com.example.prog3210_a2.adapters;

import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prog3210_a2.R;
import com.example.prog3210_a2.models.MovieDetail;

public class MovieDetailsAdapter extends RecyclerView.Adapter<MovieDetailsAdapter.ViewHolder> {
    private final MovieDetail[] details;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView, valueTextView;

        public TextView getValueTextView() {
            return valueTextView;
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public ViewHolder(View view) {
            super(view);

            nameTextView = view.findViewById(R.id.nameText);
            valueTextView = view.findViewById(R.id.valueText);
        }
    }

    public MovieDetailsAdapter(MovieDetail[] details) {
        this.details = details;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.movie_detail_cell, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        MovieDetail detail = details[position];

        viewHolder.getNameTextView().setText(detail.name);
        viewHolder.getValueTextView().setText(detail.value);
    }

    @Override
    public int getItemCount() {
        return details.length;
    }
}