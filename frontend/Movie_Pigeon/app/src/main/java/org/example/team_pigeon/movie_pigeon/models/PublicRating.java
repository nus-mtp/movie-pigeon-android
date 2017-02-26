package org.example.team_pigeon.movie_pigeon.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SHENGX on 2017/2/18.
 */

public class PublicRating implements Serializable {
    @Expose
    @SerializedName("movie_id")
    private String movieID;
    @Expose
    @SerializedName("source_id")
    private String sourceID;
    @Expose
    @SerializedName("vote")
    private Integer voteCount;
    @Expose
    @SerializedName("score")
    private String score;

    public PublicRating(){};

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public String getSourceID() {
        return sourceID;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
