package org.example.team_pigeon.movie_pigeon.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SHENGX on 2017/2/19.
 */

public class UserRating implements Serializable{
    @Expose
    @SerializedName("user_id")
    private String userId;
    @Expose
    @SerializedName("movie_id")
    private String movieId;
    @Expose
    @SerializedName("score")
    private String score;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
}
