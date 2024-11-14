package com.example.prog3210_a2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends Activity
{
    private RecyclerView moviesView;
    private EditText searchFieldView;
    private TextView statusTextView;

    private final String baseUrl = "https://www.omdbapi.com/?apikey=10849f3b";

    private void showStatusText(String text) {
        statusTextView.setVisibility(View.VISIBLE);
        statusTextView.setText(text);
    }

    private void hideStatusText() {
        statusTextView.setVisibility(View.INVISIBLE);
    }

    private void showMovies(Movie[] movies) {
        moviesView.setVisibility(View.VISIBLE);

        MoviesAdapter moviesAdapter =new MoviesAdapter(
                this.getApplicationContext(), movies);

        moviesView.setAdapter(moviesAdapter);
    }

    private void hideMovies() {
        moviesView.setVisibility(View.INVISIBLE);
    }

    private void showSearchError() {
        runOnUiThread(() -> {
            hideMovies();
            showStatusText(getResources().getString(R.string.search_error));
        });
    }

    private void showSearchFailure(JSONObject searchJson) throws JSONException {
        String errorText = searchJson.getString("Error");

        runOnUiThread(() -> {
            hideMovies();

            String searchFailureText = getResources().getString(R.string.search_failure);
            showStatusText(String.format("%s %s", searchFailureText, errorText));
        });
    }

    private JSONObject requestJson(String urlStr) throws IOException, JSONException {
        URL url = new URL(urlStr);

        Scanner urlScanner = new Scanner(url.openStream());
        String jsonStr = urlScanner.useDelimiter("\\A").next();

        return new JSONObject(jsonStr);
    }

    private Movie createMovie(JSONObject searchMovieJson) throws IOException, JSONException {
        // Request movie details
        String imdbID = searchMovieJson.getString("imdbID");
        String url = String.format("%s&i=%s", baseUrl, imdbID);
        JSONObject movieJson = requestJson(url);

        return new Movie(
                movieJson.getString("Title"),
                movieJson.getString("Director"),
                movieJson.getString("Year"),
                movieJson.getString("imdbRating"));
    }

    private void searchMoviesAsync() {
        try {
            // Request JSON
            String searchTerm = String.valueOf(searchFieldView.getText());
            String url = String.format("%s&s=%s", baseUrl, searchTerm);
            JSONObject searchJson = requestJson(url);

            // Check for failure
            if (!searchJson.getBoolean("Response")) {
                showSearchFailure(searchJson);
                return;
            }

            // Create movies list
            JSONArray searchMoviesJson = searchJson.getJSONArray("Search");

            int movieCount = Math.min(searchMoviesJson.length(), 5);
            Movie[] movies = new Movie[movieCount];

            for (int i = 0; i < movieCount; i++) {
                JSONObject searchMovieJson = searchMoviesJson.getJSONObject(i);
                movies[i] = createMovie(searchMovieJson);
            }

            // Update view
            runOnUiThread(() -> {
                showMovies(movies);
                hideStatusText();
            });
        } catch (JSONException | IOException e) {
            showSearchError();
        }
    }

    public void searchMovies(View view) {
        showStatusText(getResources().getString(R.string.search_loading));

        new Thread() {
            public void run() {
                searchMoviesAsync();
            }
        }.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        moviesView = findViewById(R.id.movieList);
        moviesView.setLayoutManager(new LinearLayoutManager(this));

        searchFieldView = findViewById(R.id.searchField);
        statusTextView = findViewById(R.id.statusText);

        hideStatusText();
    }
}