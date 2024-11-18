package com.example.prog3210_a2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prog3210_a2.MoviesApplication;
import com.example.prog3210_a2.databinding.ActivitySearchBinding;
import com.example.prog3210_a2.databinding.ActivitySearchBindingImpl;
import com.example.prog3210_a2.models.Movie;
import com.example.prog3210_a2.adapters.MovieSearchAdapter;
import com.example.prog3210_a2.R;
import com.example.prog3210_a2.models.MovieDetail;
import com.example.prog3210_a2.viewmodels.MovieSearchViewModel;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MovieSearchActivity extends AppCompatActivity
{
    private ActivitySearchBinding binding;
    private MovieSearchViewModel viewModel;

    private void showStatusText(String text) {
        binding.statusText.setVisibility(View.VISIBLE);
        binding.statusText.setText(text);
    }

    private void hideStatusText() {
        binding.statusText.setVisibility(View.INVISIBLE);
    }


    private MovieDetail createMovieDetail(JSONObject movieJson, String detail, String detailName) throws JSONException {
        return new MovieDetail(detailName, movieJson.getString(detail));
    }

    private Movie createMovie(JSONObject movieJson) throws JSONException {
        Movie movie = new Movie();

        movie.title = movieJson.getString("Title");
        movie.year = movieJson.getString("Year");
        movie.rating = movieJson.getString("Rated");
        movie.director = movieJson.getString("Director");
        movie.runtime = movieJson.getString("Runtime");
        movie.description = movieJson.getString("Plot");
        movie.posterUrl = movieJson.getString("Poster");

        JSONArray ratingsJson = movieJson.getJSONArray("Ratings");
        ArrayList<String> ratings = new ArrayList<>();

        for (int i = 0; i < ratingsJson.length(); i++) {
            JSONObject ratingJson = ratingsJson.getJSONObject(i);
            String ratingSource = ratingJson.getString("Source");

            ratings.add(String.format("%s (%s)",
                    ratingJson.getString("Value"), ratingSource));
        }

        String ratingsText = String.join(", ", ratings);

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
                new MovieDetail(
                        getResources().getString(R.string.movie_prefix_ratings), ratingsText),
                createMovieDetail(movieJson, "DVD",
                        getResources().getString(R.string.movie_prefix_dvd)),
                createMovieDetail(movieJson, "BoxOffice",
                        getResources().getString(R.string.movie_prefix_box_office)),
                createMovieDetail(movieJson, "Production",
                        getResources().getString(R.string.movie_prefix_production)),
                createMovieDetail(movieJson, "Website",
                        getResources().getString(R.string.movie_prefix_website)),
        };

        return movie;
    }

    private Movie[] createMovies(JSONObject[] movieJsons) throws IOException, JSONException {
        Movie[] movies = new Movie[movieJsons.length];

        for (int i = 0; i < movies.length; i++) {
            movies[i] = createMovie(movieJsons[i]);
        }

        return movies;
    }

    private void showMovies(Movie[] movies) {
        binding.movieList.setVisibility(View.VISIBLE);

        MovieSearchAdapter moviesAdapter = new MovieSearchAdapter(movies);
        binding.movieList.setAdapter(moviesAdapter);
    }

    private void hideMovies() {
        binding.movieList.setVisibility(View.INVISIBLE);
    }

    private void showSearchSuccess(Movie[] movies) throws JSONException, IOException {
        showMovies(movies);

        String searchResultsText = getResources().getString(R.string.search_results);
        showStatusText(String.format(searchResultsText, movies.length));
    }

    private void showSearchFailure() {
        hideMovies();

        String searchFailureText = getResources().getString(R.string.search_failure);
        String errorText;

        if (viewModel.errorText == null) {
            errorText = getResources().getString(R.string.search_error);
        }
        else {
            errorText = viewModel.errorText;
        }

        showStatusText(String.format("%s %s", searchFailureText, errorText));
    }

    public void searchMovies(View view) {
        showStatusText(getResources().getString(R.string.search_loading));
        viewModel.searchMovies(this, String.valueOf(binding.searchField.getText()));
    }

    public void showDetails(View view) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movieIndex", (int)view.getTag());

        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchBindingImpl.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        viewModel = new ViewModelProvider(this).get(MovieSearchViewModel.class);

        binding.movieList.setLayoutManager(new LinearLayoutManager(this));

        hideStatusText();

        final Observer<JSONObject[]> moviesObserver = new Observer<JSONObject[]>() {
            @Override
            public void onChanged(@Nullable final JSONObject[] movieJsons) {
                if (movieJsons == null) {
                    showSearchFailure();
                } else {
                    try {
                        Movie[] movies = createMovies(movieJsons);
                        showSearchSuccess(movies);

                        // Set movies for use across activities
                        ((MoviesApplication)getApplication()).movies = movies;
                    } catch (JSONException | IOException e) {
                        showSearchFailure();
                    }
                }
            }
        };

        viewModel.movieJsons.observe(this, moviesObserver);
    }
}