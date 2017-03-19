package org.example.team_pigeon.movie_pigeon.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by SHENGX on 2017/3/4.
 */
public class Schedule implements Serializable{
    @Expose
    @SerializedName("movie_id")
    private String movieId;
    @Expose
    @SerializedName("cinema_id")
    private int cinemaId;
    @Expose
    @SerializedName("schedule")
    private String time;
    @Expose
    @SerializedName("time")
    private String showtime;
    @Expose
    private String type;
    @Expose
    @SerializedName("date")
    private String date;
    @Expose
    @SerializedName("cinema_name")
    private String cinemaName;

    private ArrayList<String> showTimeArray;

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public int getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(int cinemaId) {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public ArrayList<String> getShowTimeArray() {
        return showTimeArray;
    }

    public void setShowTimeArray(ArrayList<String> showTimeArray) {
        this.showTimeArray = showTimeArray;
    }

    public String getShowtime() {
        return showtime;
    }

    public void setShowtime(String showtime) {
        this.showtime = showtime;
    }
}
