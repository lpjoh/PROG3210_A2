package com.example.prog3210_a2;

import java.io.Serializable;

public class Movie {
    private final String title, studio, year, rating;

    public Movie(String title, String studio, String year, String rating) {
        this.title = title;
        this.studio = studio;
        this.year = year;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public String getStudio() {
        return studio;
    }

    public String getYear() {
        return year;
    }

    public String getRating() {
        return rating;
    }


}
