package org.example.team_pigeon.movie_pigeon;
import okhttp3.Request;

/**
 * Created by SHENGX on 2017/2/8.
 */

public class SearchRequestHttpBuilder {

    private String keywords;
    private String token;
    private String url = new String("http://128.199.231.190:8080/api/movies/title");

    public SearchRequestHttpBuilder(String keywords, String token){
        this.keywords = keywords;
        this.token = token;
    }

    public Request getRequest() {
        return new Request.Builder().url(url).header("title", keywords).addHeader("Authorization", "Bearer "+ token).build();
    }
}
