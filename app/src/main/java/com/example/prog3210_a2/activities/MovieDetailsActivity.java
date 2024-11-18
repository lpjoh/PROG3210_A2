package com.example.prog3210_a2.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prog3210_a2.adapters.MovieDetailsAdapter;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MovieDetailsActivity extends AppCompatActivity
{
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
        setContentView(R.layout.activity_details);

        viewModel = new ViewModelProvider(this).get(MovieDetailsViewModel.class);

        // Get movie from application
        int movieIndex = getIntent().getIntExtra("movieIndex", -1);
        Movie movie = ((MoviesApplication)getApplication()).movies[movieIndex];

        // Show details
        showText(findViewById(R.id.titleText), movie.title);
        showText(findViewById(R.id.ratingText), movie.rating);
        showText(findViewById(R.id.yearText), movie.year);
        showText(findViewById(R.id.runtimeText), movie.runtime);
        showText(findViewById(R.id.descriptionText), movie.description);

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