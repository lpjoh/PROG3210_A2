package com.example.prog3210_a2.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prog3210_a2.adapters.MovieDetailsAdapter;
import com.example.prog3210_a2.databinding.ActivityDetailsBinding;
import com.example.prog3210_a2.models.Movie;
import com.example.prog3210_a2.MoviesApplication;
import com.example.prog3210_a2.R;
import com.example.prog3210_a2.viewmodels.MovieDetailsViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MovieDetailsActivity extends AppCompatActivity
{
    private ActivityDetailsBinding binding;
    private MovieDetailsViewModel viewModel;

    ImageView posterView;

    private void backToSearch() {
        finish();
    }

    private void showText(TextView textView, String text) {
        textView.setText(text);
    }

    public void backToSearch(View view) {
        backToSearch();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        viewModel = new ViewModelProvider(this).get(MovieDetailsViewModel.class);

        // Get movie from application
        int movieIndex = getIntent().getIntExtra("movieIndex", -1);
        Movie movie = ((MoviesApplication)getApplication()).movies[movieIndex];

        // Show details
        showText(binding.titleText, movie.title);
        showText(binding.ratingText, movie.rating);
        showText(binding.yearText, movie.year);
        showText(binding.runtimeText, movie.runtime);
        showText(binding.descriptionText, movie.description);

        RecyclerView detailsView = findViewById(R.id.detailsList);
        detailsView.setLayoutManager(new LinearLayoutManager(this));
        detailsView.setAdapter(new MovieDetailsAdapter(movie.details));

        // Load poster
        posterView = findViewById(R.id.posterImage);

        final Observer<Bitmap> posterObserver = new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable final Bitmap image) {
                posterView.setImageBitmap(image);
            }
        };

        viewModel.poster.observe(this, posterObserver);

        viewModel.loadPoster(this, movie.posterUrl);
    }
}