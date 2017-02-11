package org.example.team_pigeon.movie_pigeon;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.example.team_pigeon.movie_pigeon.models.Movie;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SHENGX on 2017/2/8.
 */

public class SearchRequestHttpBuilder {
    private Request request;
    private Response response;
    private String keywords;
    private String url = new String("http://128.199.231.190:8080/api/movies/title");
    private final OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();
    private ArrayList<Movie> movies;
    public SearchRequestHttpBuilder(String keywords){
        this.keywords = keywords;
    }
    public void run() throws IOException {
        request = new Request.Builder().url(url).header("title","%"+keywords+"%").addHeader("Authorization", "Basic c29uZ0B0ZXN0LmNvbTp0ZXN0").build();
        response = client.newCall(request).execute();
        if(!response.isSuccessful()){
            throw new IOException("Unexpected transactionId" + response);
        }
        this.movies = gson.fromJson(response.body().charStream(), new TypeToken<ArrayList<Movie>>(){}.getType());
    }

    public ArrayList<Movie> getMovies(){
        return this.movies;
    }

}
