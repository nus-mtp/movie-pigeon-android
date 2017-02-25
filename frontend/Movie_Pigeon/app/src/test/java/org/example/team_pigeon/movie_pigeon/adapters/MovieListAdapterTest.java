package org.example.team_pigeon.movie_pigeon.adapters;

import android.content.Context;
import android.test.mock.MockContext;

import org.example.team_pigeon.movie_pigeon.models.Movie;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by SHENGX on 2017/2/24.
 */
public class MovieListAdapterTest {
    Context context;
    ArrayList<Movie> movieArrayList;
    MovieListAdapter movieListAdapter;
    @Before
    public void setUp() throws Exception {
        context = new MockContext();
        movieArrayList = new ArrayList<>();
        for(int i=0;i<20;i++){
            movieArrayList.add(new Movie("test"+String.valueOf(i),String.valueOf(i)));
        }
        movieListAdapter = new MovieListAdapter(movieArrayList, context);
    }

    @Test
    public void getItem_returnCorrectMovie() {
        Movie movieReturn = movieListAdapter.getItem(10);
        assertEquals("10",movieReturn.getMovieID());
        assertEquals("test10",movieReturn.getTitle());
    }


}