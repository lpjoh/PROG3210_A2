package com.example.prog3210_a2.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prog3210_a2.models.Movie;
import com.example.prog3210_a2.models.MovieDetail;
import com.example.prog3210_a2.adapters.MovieSearchAdapter;
import com.example.prog3210_a2.MoviesApplication;
import com.example.prog3210_a2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class MovieSearchActivity extends Activity
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

        MovieSearchAdapter moviesAdapter = new MovieSearchAdapter(movies);
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

    private MovieDetail createMovieDetail(JSONObject movieJson, String detail, String detailName) throws JSONException {
        return new MovieDetail(detailName, movieJson.getString(detail));
    }

    private Movie createMovie(JSONObject searchMovieJson) throws IOException, JSONException {
        // Request movie details
        String imdbID = searchMovieJson.getString("imdbID");
        String url = String.format("%s&i=%s", baseUrl, imdbID);
        JSONObject movieJson = requestJson(url);

        Movie movie = new Movie();

        movie.title = movieJson.getString("Title");
        movie.year = movieJson.getString("Year");
        movie.rating = movieJson.getString("Rated");
        movie.director = movieJson.getString("Director");
        movie.runtime = movieJson.getString("Runtime");
        movie.description = movieJson.getString("Plot");
        movie.posterUrl = movieJson.getString("Poster");

        movie.details = new MovieDetail[] {
                createMovieDetail(movieJson, "Released",
                        getResources().getString(R.string.movie_prefix_date)),
                createMovieDetail(movieJson, "Genre",
                        getResources().getString(R.string.movie_prefix_genre)),
                createMovieDetail(movieJson, "Director",
                        getResources().getString(R.string.movie_prefix_director)),
                createMovieDetail(movieJson, "Writer",
                        getResources().getString(R.string.movie_prefix_writer)),
                createMovieDetail(movieJson, "Actors",
                        getResources().getString(R.string.movie_prefix_actors)),
                createMovieDetail(movieJson, "Language",
                        getResources().getString(R.string.movie_prefix_language)),
                createMovieDetail(movieJson, "Country",
                        getResources().getString(R.string.movie_prefix_country)),
                createMovieDetail(movieJson, "Awards",
                        getResources().getString(R.string.movie_prefix_awards)),
        };

        return movie;
    }

    private void searchMoviesAsync() {
        try {
            // Request JSON
            String searchTerm = String.valueOf(searchFieldView.getText());
            String url = String.format("%s&type=movie&s=%s", baseUrl, searchTerm);
            JSONObject searchJson = requestJson(url);

            // Check for failure
            if (!searchJson.getBoolean("Response")) {
                showSearchFailure(searchJson);
                return;
            }

            // Create movies list
            JSONArray searchMoviesJson = searchJson.getJSONArray("Search");

            int movieCount = searchMoviesJson.length();
            Movie[] movies = new Movie[movieCount];

            for (int i = 0; i < movieCount; i++) {
                JSONObject searchMovieJson = searchMoviesJson.getJSONObject(i);
                movies[i] = createMovie(searchMovieJson);
            }

            ((MoviesApplication)getApplication()).movies = movies;

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

    public void showDetails(View view) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);

        intent.putExtra("movieIndex", (int)view.getTag());

        startActivity(intent);
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