package org.example.team_pigeon.movie_pigeon;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by SHENGX on 2017/2/8.
 */

public class ListRequestHttpBuilderSingleton {
    private static ListRequestHttpBuilderSingleton instance = null;
    private OkHttpClient client = null;
    private String keywords;
    private String token;
    private int offset = 0;
    private final int LIMIT = 20;
    private final String url = new String("http://128.199.231.190:8080/api/movies/title");

    protected ListRequestHttpBuilderSingleton() {}

    public static ListRequestHttpBuilderSingleton getInstance() {
        if(instance == null) {
            instance = new ListRequestHttpBuilderSingleton();
        }
        return instance;
    }

    public Request getSearchRequest() {
        setOffset(0);
        return new Request.Builder().url(url).header("title", keywords.trim()).
                addHeader("limit", String.valueOf(LIMIT).trim()).
                addHeader("offset", String.valueOf(offset).trim()).
                addHeader("Authorization", "Bearer "+ token.trim()).build();
    }

    public Request getNextSearchRequest() {
        setOffset(getOffset()+getLimit());
        return new Request.Builder().url(url).header("title", keywords.trim()).
                addHeader("limit", String.valueOf(LIMIT).trim()).
                addHeader("offset", String.valueOf(offset).trim()).
                addHeader("Authorization", "Bearer "+ token.trim()).build();
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
            client = new OkHttpClient();
        }
        return client;
    }

    public int getLimit() {
        return LIMIT;
    }
}
