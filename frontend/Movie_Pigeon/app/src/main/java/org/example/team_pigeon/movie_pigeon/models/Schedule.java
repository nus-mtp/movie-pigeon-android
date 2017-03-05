package org.example.team_pigeon.movie_pigeon.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SHENGX on 2017/3/4.
 */
public class Schedule implements Serializable{
    @Expose
    @SerializedName("movie_id")
    private String movieId;
    @Expose
    @SerializedName("cinema_id")
    private String cinemaId;
    @Expose
    @SerializedName("schedule")
    private String time;
    @Expose
    private String type;

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(String cinemaId) {
        this.cinemaId = cinemaId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
