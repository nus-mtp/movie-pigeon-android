package org.example.team_pigeon.movie_pigeon.models;

/**
 * Created by SHENGX on 2017/2/3.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Movie implements Serializable{
    @Expose
    private String title;
    @Expose
    @SerializedName("movie_id")
    private String movieID;
    @Expose
    @SerializedName("production_year")
    private String productionYear;
    @Expose
    private String language;
    @Expose
    private String country;
    @Expose
    @SerializedName("poster_url")
    private String posterURL;
    @Expose
    private String director;
    @Expose
    @SerializedName("rated")
    private String rating;
    @Expose
    @SerializedName("runtime")
    private String length;
    @Expose
    private String plot;
    @Expose
    private String actors;
    @Expose
    @SerializedName("genre")
    private String genres;
    @Expose
    @SerializedName("public_ratings")
    private ArrayList<PublicRating> publicRatings;
    @Expose
    @SerializedName("user_ratings")
    private ArrayList<UserRating> userRating;
    @Expose
    @SerializedName("bookmarks")
    private ArrayList<UserBookmark> userBookmark;
    private String tempRating;
    @Expose
    @SerializedName("showings")
    private ArrayList<Schedule> schedule;
    private boolean isRated;
    private boolean isReleased;
    private boolean isBookmarked;

    public Movie(String title,String movieID){
        this.title = title;
        this.movieID = movieID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public String getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(String productionYear) {
        this.productionYear = productionYear;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public boolean isRated() {
        return isRated;
    }

    public void setRated(boolean rated) {
        isRated = rated;
    }

    public boolean isReleased() {
        return isReleased;
    }

    public void setReleased(boolean released) {
        isReleased = released;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public ArrayList<PublicRating> getPublicRatingses() {
        return publicRatings;
    }

    public void setPublicRatingses(ArrayList<PublicRating> publicRatings) {
        this.publicRatings = publicRatings;
    }

    public ArrayList<UserRating> getUserRating() {
        return userRating;
    }

    public void setUserRating(ArrayList<UserRating> userRating) {
        this.userRating = userRating;
    }

    public String getTempRating() {
        return tempRating;
    }

    public void setTempRating(String tempRating) {
        this.tempRating = tempRating;
    }

    public ArrayList<UserBookmark> getUserBookmark() {
        return userBookmark;
    }

    public void setUserBookmark(ArrayList<UserBookmark> userBookmark) {
        this.userBookmark = userBookmark;
    }

    public ArrayList<Schedule> getSchedule() {
        return schedule;
    }

    public void setSchedule(ArrayList<Schedule> schedule) {
        this.schedule = schedule;
    }
}
