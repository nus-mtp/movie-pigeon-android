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

public class MovieListAdapter extends BaseAdapter {
    private static final int TYPE_MOVIE = 0;
    private static final int TYPE_SEPARATOR = 1;
    private ArrayList<Movie> movieList;
    private TreeSet separatorSet = new TreeSet();
    private Context mContext;
    private Movie movie;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = new ImageConfig().getDisplayImageOption();

    public MovieListAdapter(ArrayList<Movie> movieList, Context mContext) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    @Override
    public int getItemViewType(int position) {
        return separatorSet.contains(position) ? TYPE_SEPARATOR : TYPE_MOVIE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void addListItemToAdapter(ArrayList<Movie> list) {
        movieList.addAll(list);
        this.notifyDataSetChanged();
    }

    public void addHeaderToAdapter(final Movie separator) {
        movieList.add(separator);
        separatorSet.add(movieList.size() - 1);
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
        int type = getItemViewType(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            switch (type) {
                case TYPE_MOVIE:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, null);
                    viewHolder.txt_title = (TextView) convertView.findViewById(R.id.text_movie_list_title);
                    viewHolder.image_poster = (ImageView) convertView.findViewById(R.id.image_movie_list_poster);
                    viewHolder.txt_year = (TextView) convertView.findViewById(R.id.text_movie_list_year);
                    viewHolder.txt_genre = (TextView) convertView.findViewById(R.id.text_movie_list_genre);
                    viewHolder.txt_length = (TextView) convertView.findViewById(R.id.text_movie_list_length);
                    viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar_movie_list);
                    break;
                case TYPE_SEPARATOR:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.header_all_search_list, null);
                    convertView.setClickable(false);
                    break;
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //If viewHolder's child view is binded, it is not a separator, and show data
        if (viewHolder.txt_title != null) {
            viewHolder.txt_title.setText(movie.getTitle());
            imageLoader.displayImage(movieList.get(position).getPosterURL(), viewHolder.image_poster, options);

            if (movie.getProductionYear() != null && !movie.getProductionYear().equals("")) {
                viewHolder.txt_year.setVisibility(View.VISIBLE);
                viewHolder.txt_year.setText(movie.getProductionYear());
            } else {
                viewHolder.txt_year.setVisibility(View.GONE);
            }

            if (movie.getGenres() != null && !movie.getGenres().equals("")) {
                viewHolder.txt_genre.setVisibility(View.VISIBLE);
                viewHolder.txt_genre.setText(movie.getGenres());
            } else {
                viewHolder.txt_year.setVisibility(View.GONE);
            }

            if (movie.getLength() != null && !movie.getLength().equals("")) {
                viewHolder.txt_length.setVisibility(View.VISIBLE);
                viewHolder.txt_length.setText(movie.getLength()+" Min");
            } else {
                viewHolder.txt_length.setVisibility(View.GONE);
            }

            if (!movie.getPublicRatingses().isEmpty()) {
                Float averageRating = new Float(0);
                int ratingCount = 0;
                ArrayList<PublicRating> ratings = movie.getPublicRatingses();
                for (PublicRating rating : ratings) {
                    if(rating.getScore()!=null && !rating.getScore().equals("0")) {
                        ratingCount++;
                        averageRating += Float.valueOf(rating.getScore());
                    }
                }
                if(ratingCount==0){
                    viewHolder.ratingBar.setVisibility(View.INVISIBLE);
                }
                else {
                    averageRating = averageRating / ratingCount;
                    viewHolder.ratingBar.setVisibility(View.VISIBLE);
                    viewHolder.ratingBar.setNumStars(5);
                    viewHolder.ratingBar.setRating(averageRating / 2);
                }
            } else {
                viewHolder.ratingBar.setVisibility(View.INVISIBLE);
            }
        }


        return convertView;
    }

    private static class ViewHolder {
        TextView txt_title;
        ImageView image_poster;
        TextView txt_length;
        TextView txt_genre;
        TextView txt_year;
        RatingBar ratingBar;
    }
}
