package org.example.team_pigeon.movie_pigeon.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by SHENGX on 2017/2/19.
 */

public class MovieWithCount {
    @Expose
    @SerializedName("count")
    private int count;
    @Expose
    @SerializedName("raw")
    private ArrayList<Movie> movies;

    public MovieWithCount(int count, ArrayList<Movie> movies) {
        this.count = count;
        this.movies = movies;
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
