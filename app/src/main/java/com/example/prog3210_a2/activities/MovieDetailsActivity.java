package com.example.prog3210_a2.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.prog3210_a2.adapters.MovieDetailsAdapter;
import com.example.prog3210_a2.models.Movie;
import com.example.prog3210_a2.MoviesApplication;
import com.example.prog3210_a2.R;
import com.example.prog3210_a2.models.MovieDetail;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MovieDetailsActivity extends Activity
{
    ImageView posterView;

    private void backToSearch() {
        finish();
    }

    private void showText(TextView textView, String text) {
        textView.setText(text);
    }

    private void loadPosterAsync(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            InputStream is = connection.getInputStream();
            Bitmap img = BitmapFactory.decodeStream(is);

            runOnUiThread(() -> {
                posterView.setImageBitmap(img);
            });
        }
        catch (IOException e) {
            return;
        }
    }

    public void backToSearch(View view) {
        backToSearch();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Get movie from application
        int movieIndex = getIntent().getIntExtra("movieIndex", -1);
        Movie movie = ((MoviesApplication)getApplication()).movies[movieIndex];

        // Show details
        showText(findViewById(R.id.titleText), movie.title);
        showText(findViewById(R.id.ratingText), movie.rating);
        showText(findViewById(R.id.yearText), movie.year);
        showText(findViewById(R.id.runtimeText), movie.runtime);
        showText(findViewById(R.id.descriptionText), movie.description);

        String[] detailTexts = new String[movie.details.length];

        for (int i = 0; i < detailTexts.length; i++) {
            MovieDetail detail = movie.details[i];
            detailTexts[i] = String.format("<b>%s</b> %s", detail.name, detail.value);
        }

        RecyclerView detailsView = findViewById(R.id.detailsList);
        detailsView.setLayoutManager(new LinearLayoutManager(this));
        detailsView.setAdapter(new MovieDetailsAdapter(detailTexts));

        // Load poster
        posterView = findViewById(R.id.posterImage);

        new Thread() {
            public void run() {
                loadPosterAsync(movie.posterUrl);
            }
        }.start();
    }
}