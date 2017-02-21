package org.example.team_pigeon.movie_pigeon.eventCenter;

import org.example.team_pigeon.movie_pigeon.models.Movie;

/**
 * Created by SHENGX on 2017/2/21.
 */

public class AddMovieToMovieListEvent {
    public int position;
    public Movie movie;

    public AddMovieToMovieListEvent(int position, Movie movie){
        this.movie = movie;
        this.position = position;
    }
}
