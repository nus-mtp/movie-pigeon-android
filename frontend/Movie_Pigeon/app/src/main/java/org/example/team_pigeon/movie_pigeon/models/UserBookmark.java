package org.example.team_pigeon.movie_pigeon.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SHENGX on 2017/2/20.
 */

public class UserBookmark implements Serializable {
    @Expose
    @SerializedName("user_id")
    private String userId;
    @Expose
    @SerializedName("movie_id")
    private String movieId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
}
