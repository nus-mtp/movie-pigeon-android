package org.example.team_pigeon.movie_pigeon.eventCenter;

import org.example.team_pigeon.movie_pigeon.models.Movie;

/**
 * Created by SHENGX on 2017/2/21.
 */

public class UpdateMovieListEvent {
    public int position;
    public Movie movie;
    public UpdateMovieListEvent(int position, Movie movie) {
        this.position = position;
        this.movie = movie;
    }
}

