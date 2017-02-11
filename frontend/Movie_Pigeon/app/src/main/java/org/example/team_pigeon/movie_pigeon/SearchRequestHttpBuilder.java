package org.example.team_pigeon.movie_pigeon;
import okhttp3.Request;

/**
 * Created by SHENGX on 2017/2/8.
 */

public class SearchRequestHttpBuilder {

    private String keywords;
    private String url = new String("http://128.199.231.190:8080/api/movies/title");

    public SearchRequestHttpBuilder(String keywords){
        this.keywords = keywords;
    }

    public Request getRequest() {
        return new Request.Builder().url(url).header("title", keywords).addHeader("Authorization", "Basic c29uZ0B0ZXN0LmNvbTp0ZXN0").build();
    }
}
