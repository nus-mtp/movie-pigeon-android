package org.example.team_pigeon.movie_pigeon.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.example.team_pigeon.movie_pigeon.DisplayActivity;
import org.example.team_pigeon.movie_pigeon.R;
import org.example.team_pigeon.movie_pigeon.configs.ImageConfig;
import org.example.team_pigeon.movie_pigeon.models.Movie;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by SHENGX on 2017/3/1.
 */

public class NowShowingListAdapter extends BaseAdapter {
    private ArrayList<Movie> movieList;
    private Context mContext;
    private Movie movie;
    private Date date;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = new ImageConfig().getDisplayImageOption();

    public NowShowingListAdapter(ArrayList<Movie> movieList, Context mContext) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    public void addListItemToAdapter(ArrayList<Movie> list) {
        movieList.addAll(list);
        this.notifyDataSetChanged();
    }

    public void removeMovieItemToAdapter(int position) {
        movieList.remove(position);
        this.notifyDataSetChanged();
    }

    public void addMovieItemToAdapter(Movie movie, int position) {
        movieList.add(position, movie);
        this.notifyDataSetChanged();
    }

    public void updateMovieItemToAdapter(Movie movieNew, int position) {
        movieList.set(position, movieNew);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Movie getItem(int position) {
        return movieList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final int parentPosition = position;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.now_showing_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txt_title = (TextView) convertView.findViewById(R.id.text_now_showing_list_title);
            viewHolder.image_poster = (ImageView) convertView.findViewById(R.id.image_now_showing_list_poster);
            viewHolder.txt_length = (TextView) convertView.findViewById(R.id.text_now_showing_list_length);
            viewHolder.txt_special = (TextView) convertView.findViewById(R.id.text_now_showing_list_special);
            viewHolder.grid_schedule = (GridView) convertView.findViewById(R.id.grid_schedule);
            viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar_now_showing);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        movie = movieList.get(position);
        viewHolder.txt_title.setText(movie.getTitle());
        viewHolder.txt_special.setText(movie.getSchedule().get(0).getType());
        viewHolder.txt_length.setText(movie.getLength() + " Min");
        setRatingBar(viewHolder.ratingBar, movie);
        viewHolder.grid_schedule.setAdapter(new ArrayAdapter<>(this.mContext, R.layout.now_showing_schedule_cell, movie.getShowTimes()));
        viewHolder.grid_schedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(parentPosition);
            }
        });
        imageLoader.displayImage(movieList.get(position).getPosterURL(), viewHolder.image_poster, options);
        return convertView;
    }

    private void startActivity(int position){
        Intent displayActivityIntent = new Intent(this.mContext, DisplayActivity.class);
        Bundle arguments = new Bundle();
        arguments.putSerializable("movie",movieList.get(position));
        arguments.putString("type", "moviePage");
        arguments.putInt("position",position);
        arguments.putString("title", movieList.get(position).getTitle());
        displayActivityIntent.putExtra("bundle", arguments);
        this.mContext.startActivity(displayActivityIntent);
    }

    private void setRatingBar(RatingBar ratingBar, Movie movie) {
        if (!movie.getPublicRatingses().isEmpty()) {
            Float ratting = Float.valueOf(movie.getPublicRatingses().get(0).getScore());
            ratingBar.setNumStars(5);
            ratingBar.setRating(ratting / 2);
        } else {
            ratingBar.setVisibility(View.INVISIBLE);
        }
    }

    private static class ViewHolder {
        TextView txt_title;
        TextView txt_length;
        TextView txt_special;
        ImageView image_poster;
        GridView grid_schedule;
        RatingBar ratingBar;
    }
}
