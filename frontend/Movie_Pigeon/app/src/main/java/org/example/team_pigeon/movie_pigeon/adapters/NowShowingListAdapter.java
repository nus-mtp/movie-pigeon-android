package org.example.team_pigeon.movie_pigeon.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.example.team_pigeon.movie_pigeon.R;
import org.example.team_pigeon.movie_pigeon.configs.ImageConfig;
import org.example.team_pigeon.movie_pigeon.models.Movie;
import org.example.team_pigeon.movie_pigeon.models.Schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    public NowShowingListAdapter(ArrayList<Movie> movieList, Date date, Context mContext) {
        this.mContext = mContext;
        this.movieList = movieList;
        this.date = date;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        NowShowingListAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            ArrayList<String> scheduleList = new ArrayList<>();
            movie = movieList.get(position);
            try {
                scheduleList = getScheduleList(date, movie.getSchedule());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (scheduleList.isEmpty()) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.empty_item, parent, false);
                convertView.setVisibility(View.INVISIBLE);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.now_showing_list_item, parent, false);
                viewHolder = new NowShowingListAdapter.ViewHolder();
                convertView.setVisibility(View.VISIBLE);
                viewHolder.txt_title = (TextView) convertView.findViewById(R.id.text_now_showing_list_title);
                viewHolder.image_poster = (ImageView) convertView.findViewById(R.id.image_now_showing_list_poster);
                viewHolder.txt_length = (TextView) convertView.findViewById(R.id.text_now_showing_list_length);
                viewHolder.txt_special = (TextView) convertView.findViewById(R.id.text_now_showing_list_special);
                viewHolder.grid_schedule = (GridView) convertView.findViewById(R.id.grid_schedule);
                viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar_now_showing);
                convertView.setTag(viewHolder);
                viewHolder.txt_title.setText(movie.getTitle());
                viewHolder.txt_special.setText(movie.getSchedule().get(0).getType());
                viewHolder.txt_length.setText(movie.getLength() + " Min");
                setRatingBar(viewHolder.ratingBar, movie);

                viewHolder.grid_schedule.setAdapter(new ArrayAdapter<>(this.mContext, R.layout.now_showing_schedule_cell, scheduleList));
                viewHolder.grid_schedule.setEnabled(false);
                imageLoader.displayImage(movieList.get(position).getPosterURL(), viewHolder.image_poster, options);
            }
        } else {
            viewHolder = (NowShowingListAdapter.ViewHolder) convertView.getTag();
        }
        return convertView;
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

    private ArrayList<String> getScheduleList(Date date, ArrayList<Schedule> schedules) throws ParseException {
        String timeString;
        Calendar calendarOne = Calendar.getInstance();
        Calendar calendarTwo = Calendar.getInstance();
        calendarOne.setTime(date);
        ArrayList<String> showTimeList = new ArrayList<>();
        Date time;
        SimpleDateFormat stringToDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat dateToString = new SimpleDateFormat("HH:mm");
        for (Schedule schedule : schedules) {
            timeString = schedule.getTime();
            time = stringToDate.parse(timeString);
            calendarTwo.setTime(time);
            if (calendarOne.get(Calendar.DAY_OF_MONTH) == calendarTwo.get(Calendar.DAY_OF_MONTH)) {
                showTimeList.add(dateToString.format(time));
            }
        }
        return showTimeList;
    }
}
