package org.example.team_pigeon.movie_pigeon.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.example.team_pigeon.movie_pigeon.R;
import org.example.team_pigeon.movie_pigeon.configs.ImageConfig;
import org.example.team_pigeon.movie_pigeon.models.Movie;

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
        if (viewHolder.txt_title != null) {
            viewHolder.txt_title.setText(movie.getTitle());
            imageLoader.displayImage(movieList.get(position).getPosterURL(), viewHolder.image_poster, options);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView txt_title;
        ImageView image_poster;
    }
}
