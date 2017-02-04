package org.example.team_pigeon.movie_pigeon.models;

/**
 * Created by SHENGX on 2017/2/3.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Movie implements Serializable{
    private String title;
    private String movieID;
    private String productionYear;
    private String language;
    private String country;
    private String posterURL;
    private String director;
    private String rating;
    private String length;
    private List<String> actors;
    private List<String> genres;
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

    public List<String> getActors() {
        return actors;
    }

    public void setActors(String actorsRaw) {
        this.actors = new ArrayList<String>(Arrays.asList(actorsRaw.split(",")));
    }

    public List<String> getGenre() {
        return genres;
    }

    public void setGenre(String genresRaw) {
        this.genres = new ArrayList<String>(Arrays.asList(genresRaw.split(",")));
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
}
