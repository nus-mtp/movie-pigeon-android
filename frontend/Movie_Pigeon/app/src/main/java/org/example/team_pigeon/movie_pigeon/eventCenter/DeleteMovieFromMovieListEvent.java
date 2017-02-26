package org.example.team_pigeon.movie_pigeon.eventCenter;

/**
 * Created by SHENGX on 2017/2/21.
 */

public class DeleteMovieFromMovieListEvent {
    public int position;
    public DeleteMovieFromMovieListEvent(int position){
        this.position = position;
    }
}
