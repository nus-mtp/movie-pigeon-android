package org.example.team_pigeon.movie_pigeon;

import org.junit.Test;

import okhttp3.Request;

import static org.junit.Assert.*;

/**
 * Created by SHENGX on 2017/2/24.
 */
public class RequestHttpBuilderSingletonTest {
    RequestHttpBuilderSingleton builderSingleton = RequestHttpBuilderSingleton.getInstance();
    String token = "test_token";
    String keywords = "test_keywords";

    @Test
    public void getInstance_returnSameInstance(){
        RequestHttpBuilderSingleton newSingleton = RequestHttpBuilderSingleton.getInstance();
        assertEquals(builderSingleton,newSingleton);
    }

    @Test
    public void getLimit_return20(){
        assertEquals(20, builderSingleton.getLimit());
    }

    @Test
    public void getSearchRequest_CorrectHeaders(){
        builderSingleton.setToken(token);
        builderSingleton.setKeywords(keywords);
        Request request = builderSingleton.getSearchRequest();
        assertEquals(builderSingleton.getKeywords(),request.header("title"));
        //When a search is initialised, the offset should be 0;
        assertEquals(String.valueOf(0),request.header("offset"));
        assertEquals("Bearer "+builderSingleton.getToken(),request.header("Authorization"));
    }

    @Test
    public void getNextSearchRequest_CorrectHeaders(){
        builderSingleton.setToken(token);
        builderSingleton.setKeywords(keywords);
        int i;
        Request request = null;
        for(i = 0; i<3; i++){
            request = builderSingleton.getNextSearchRequest();
        }
        assertEquals(builderSingleton.getLimit()*i,builderSingleton.getOffset());
        assertEquals("Bearer "+builderSingleton.getToken(),request.header("Authorization"));
        assertEquals(builderSingleton.getSearchUrl(),request.url().toString());
    }

    @Test
    public void getPostBookmarkRequest_CorrectHeaders(){
        builderSingleton.setToken(token);
        Request request = builderSingleton.getPostBookmarkRequest("testId");
        assertEquals(builderSingleton.getBookmarkUrl(),request.url().toString());
        assertEquals("Bearer "+builderSingleton.getToken(),request.header("Authorization"));
    }

    @Test
    public void getPostUnbookmarkRequest_CorrectHeaders(){
        builderSingleton.setToken(token);
        Request request = builderSingleton.getPostUnbookmarkRequest("testId");
        assertEquals(builderSingleton.getBookmarkUrl(),request.url().toString());
        assertEquals("Bearer "+builderSingleton.getToken(),request.header("Authorization"));
    }

    @Test
    public void getPostRatingRequest_CorrectHeaders(){
        builderSingleton.setToken(token);
        Request request = builderSingleton.getPostRatingRequest("testId","1.0");
        assertEquals(builderSingleton.getRatingUrl(),request.url().toString());
        assertEquals("Bearer "+builderSingleton.getToken(),request.header("Authorization"));
    }

    @Test
    public void getRatingListRequest_CorrectHeaders(){
        builderSingleton.setToken(token);
        Request request = builderSingleton.getRatingListRequest();
        assertEquals(builderSingleton.getRatingUrl(),request.url().toString());
        assertEquals("Bearer "+builderSingleton.getToken(),request.header("Authorization"));
    }

    @Test
    public void getBookmarkListRequest_CorrectHeaders(){
        builderSingleton.setToken(token);
        Request request = builderSingleton.getBookmarkListRequest();
        assertEquals(builderSingleton.getBookmarkUrl(),request.url().toString());
        assertEquals("Bearer "+builderSingleton.getToken(),request.header("Authorization"));
    }

}