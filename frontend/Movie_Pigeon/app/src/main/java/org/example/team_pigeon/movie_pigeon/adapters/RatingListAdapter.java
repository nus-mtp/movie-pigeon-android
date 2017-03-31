package org.example.team_pigeon.movie_pigeon.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.example.team_pigeon.movie_pigeon.R;
import org.example.team_pigeon.movie_pigeon.configs.ImageConfig;
import org.example.team_pigeon.movie_pigeon.models.Movie;
import org.example.team_pigeon.movie_pigeon.models.PublicRating;

import java.util.ArrayList;
import java.util.TreeSet;

public class RatingListAdapter extends BaseAdapter {
    private ArrayList<Movie> movieList;
    private Context mContext;
    private Movie movie;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = new ImageConfig().getDisplayImageOption();

    public RatingListAdapter(ArrayList<Movie> movieList, Context mContext) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        movie = movieList.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.rating_list_item, null);
            viewHolder.txt_title = (TextView) convertView.findViewById(R.id.text_rating_list_title);
            viewHolder.image_poster = (ImageView) convertView.findViewById(R.id.image_rating_list_poster);
            viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar_rating_list);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txt_title.setText(movie.getTitle());
        imageLoader.displayImage(movieList.get(position).getPosterURL(), viewHolder.image_poster, options);

        if (!movie.getUserRating().isEmpty() && movie.getUserRating().get(0).getScore()!=null){
            Float rating = Float.valueOf(movie.getUserRating().get(0).getScore());
            viewHolder.ratingBar.setNumStars(5);
            viewHolder.ratingBar.setRating(rating / 2);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView txt_title;
        ImageView image_poster;
        RatingBar ratingBar;
    }
}
