package org.example.team_pigeon.movie_pigeon.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.example.team_pigeon.movie_pigeon.R;
import org.example.team_pigeon.movie_pigeon.models.Movie;

import java.util.ArrayList;

public class MovieListAdapter extends BaseAdapter {
    private ArrayList<Movie> movieList;
    private Context mContext;
    private Movie movie;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public MovieListAdapter(ArrayList<Movie>movieList,Context mContext) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    public void addListItemToAdapter(ArrayList<Movie> list) {
        movieList.addAll(list);
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
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txt_title = (TextView) convertView.findViewById(R.id.text_movie_list_title);
            viewHolder.image_poster = (ImageView) convertView.findViewById(R.id.image_movie_list_poster);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        movie = movieList.get(position);
        viewHolder.txt_title.setText(movie.getTitle());

        if(!movie.getPosterURL().equals("NULL")){
            imageLoader.displayImage(movieList.get(position).getPosterURL(),viewHolder.image_poster);
        }
        else{
            viewHolder.image_poster.setImageResource(R.mipmap.image_no_poster_found);
        }

        return convertView;
    }

        private static class ViewHolder {
            TextView txt_title;
            ImageView image_poster;
        }
    }
