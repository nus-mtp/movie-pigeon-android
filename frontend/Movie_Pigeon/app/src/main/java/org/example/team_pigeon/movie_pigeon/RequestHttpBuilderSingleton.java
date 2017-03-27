package org.example.team_pigeon.movie_pigeon;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by SHENGX on 2017/2/8.
 */

public class RequestHttpBuilderSingleton {
    private static RequestHttpBuilderSingleton instance = null;
    private OkHttpClient client = null;
    private String keywords = "";
    private String token = "";
    private int offset = 0;
    private final int LIMIT = 20;
    private final String searchUrl = "http://128.199.167.57:8080/api/movies/title";
    private final String ratingUrl = "http://128.199.167.57:8080/api/ratings";
    private final String bookmarkUrl = "http://128.199.167.57:8080/api/bookmarks";
    private final String cinemaUrl = "http://128.199.167.57:8080/api/cinemas";
    private final String nowShowingUrl = "http://128.199.167.57:8080/api/showing";
    private final String recommendationUrl = "http://128.199.167.57:8080/api/recommendations";
    private final String nowShowingHomePageUrl = "http://128.199.167.57:8080/api/showing/all";
    private final String movieScheduleUrl = "http://128.199.167.57:8080/api/movies/schedule";

    protected RequestHttpBuilderSingleton() {}

    public static RequestHttpBuilderSingleton getInstance() {
        if(instance == null) {
            instance = new RequestHttpBuilderSingleton();
        }
        return instance;
    }

    public Request getSearchRequest() {
        setOffset(0);
        return new Request.Builder().url(searchUrl).header("title", keywords.trim()).
                addHeader("limit", String.valueOf(LIMIT).trim()).
                addHeader("offset", String.valueOf(offset).trim()).
                addHeader("Authorization", "Bearer "+ token.trim()).build();
    }

    public Request getNextSearchRequest() {
        setOffset(getOffset()+getLimit());
        return new Request.Builder().url(searchUrl).header("title", keywords.trim()).
                addHeader("limit", String.valueOf(LIMIT).trim()).
                addHeader("offset", String.valueOf(offset).trim()).
                addHeader("Authorization", "Bearer "+ token.trim()).build();
    }

    public Request getPostRatingRequest(String movieId, String score) {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        bodyBuilder.add("movieId",movieId.trim()).add("score",score.trim());
        return new Request.Builder().url(ratingUrl).
                header("Authorization", "Bearer "+ token.trim()).
                post(bodyBuilder.build()).build();
    }

    public  Request getPostBookmarkRequest(String movieId) {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        bodyBuilder.add("movieId",movieId.trim());
        return new Request.Builder().url(bookmarkUrl).
                header("Authorization", "Bearer "+ token.trim()).
                post(bodyBuilder.build()).build();
    }

    public  Request getPostUnbookmarkRequest(String movieId) {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        bodyBuilder.add("movieId",movieId.trim());
        return new Request.Builder().url(bookmarkUrl).
                header("Authorization", "Bearer "+ token.trim()).
                delete(bodyBuilder.build()).build();
    }

    public Request getRatingListRequest(){
        return new Request.Builder().url(ratingUrl).header("Authorization", "Bearer "+ token.trim()).build();
    }

    public Request getNowShowingListRequest(){
        return new Request.Builder().url(nowShowingHomePageUrl).header("Authorization", "Bearer "+ token.trim()).build();
    }

    public Request getRecommendationRequest(){
        return new Request.Builder().url(recommendationUrl).header("Authorization", "Bearer "+ token.trim()).build();
    }

    public Request getBookmarkListRequest(){
        return new Request.Builder().url(bookmarkUrl).header("Authorization", "Bearer "+ token.trim()).build();
    }

    public Request getCinemasRequest(){
        return new Request.Builder().url(cinemaUrl).header("Authorization", "Bearer "+ token.trim()).build();
    }

    public Request getShowingListRequest(String cinemaId){
        return new Request.Builder().url(nowShowingUrl).header("Authorization", "Bearer "+ token.trim()).addHeader("cinema_id",cinemaId).build();
    }

    public Request getScheduleOfMovie(String movieId){
        return new Request.Builder().url(movieScheduleUrl).header("Authorization", "Bearer "+ token.trim()).addHeader("movie_id",movieId).build();
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public OkHttpClient getClient() {
        if(client == null){
            //New client with timeout setting
            client = new OkHttpClient.Builder().
                    connectTimeout(10, TimeUnit.SECONDS).
                    readTimeout(10,TimeUnit.SECONDS).
                    writeTimeout(10,TimeUnit.SECONDS).
                    build();
        }
        return client;
    }

    public int getLimit() {
        return LIMIT;
    }

    public String getSearchUrl() {
        return searchUrl;
    }

    public String getRatingUrl() {
        return ratingUrl;
    }

    public String getBookmarkUrl() {
        return bookmarkUrl;
    }
}
