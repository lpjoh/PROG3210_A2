package com.example.prog3210_a2.viewmodels;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class MovieSearchViewModel extends ViewModel {
    public MutableLiveData<JSONObject[]> movieJsons = new MutableLiveData<>();
    public String errorText;

    private JSONObject requestJson(String urlStr) throws IOException, JSONException {
        URL url = new URL(urlStr);

        Scanner urlScanner = new Scanner(url.openStream());
        String jsonStr = urlScanner.useDelimiter("\\A").next();

        return new JSONObject(jsonStr);
    }

    private void setMovieJsons(AppCompatActivity activity, JSONObject[] value) {
        activity.runOnUiThread(() -> {
            movieJsons.setValue(value);
        });
    }

    private void searchMoviesAsync(AppCompatActivity activity, String urlStr) {
        try {
            String baseUrl = "https://www.omdbapi.com/?apikey=10849f3b";

            // Request search JSON
            String searchTerm = String.valueOf(urlStr);
            String searchUrl = String.format("%s&type=movie&s=%s", baseUrl, searchTerm);

            JSONObject searchJson = requestJson(searchUrl);

            // Check for failure
            if (!searchJson.getBoolean("Response")) {
                errorText = searchJson.getString("Error");
                setMovieJsons(activity, null);

                return;
            }

            // Create movies list
            JSONArray searchMoviesJson = searchJson.getJSONArray("Search");

            int movieCount = searchMoviesJson.length();
            JSONObject[] movieJsonsList = new JSONObject[movieCount];

            for (int i = 0; i < movieCount; i++) {
                JSONObject searchMovieJson = searchMoviesJson.getJSONObject(i);

                // Get movie URL
                String imdbID = searchMovieJson.getString("imdbID");
                String movieUrl = String.format("%s&i=%s", baseUrl, imdbID);

                // Request movie
                JSONObject movieJson = requestJson(movieUrl);
                movieJsonsList[i] = movieJson;
            }

            setMovieJsons(activity, movieJsonsList);
        } catch (JSONException | IOException e) {
            errorText = null;
            setMovieJsons(activity, null);
        }
    }

    public void searchMovies(AppCompatActivity activity, String urlStr) {
        new Thread() {
            public void run() {
                searchMoviesAsync(activity, urlStr);
            }
        }.start();
    }
}