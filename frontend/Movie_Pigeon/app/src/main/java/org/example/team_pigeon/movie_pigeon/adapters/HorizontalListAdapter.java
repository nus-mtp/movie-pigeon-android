package org.example.team_pigeon.movie_pigeon.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;

/**
 * Created by SHENGX on 2017/3/11.
 */

public class HorizontalListAdapter extends BaseAdapter {
    private ArrayList<Movie> movies;
    private Context mContext;
    private Movie movie;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = new ImageConfig().getDisplayImageOption();

    public  HorizontalListAdapter(Context context, ArrayList<Movie> movies){
        this.movies = movies;
        mContext=context;
    }
    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateMovieItemToAdapter(Movie movieNew, int position) {
        movies.set(position, movieNew);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.horizontal_list_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.txt_title = (TextView) convertView.findViewById(R.id.text_title_horizontal_list_item);
            viewHolder.image_poster = (ImageView) convertView.findViewById(R.id.image_poster_horizontal_list_item);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        movie = movies.get(position);
        viewHolder.txt_title.setText(movie.getTitle());
        imageLoader.displayImage(movies.get(position).getPosterURL(), viewHolder.image_poster, options);
        return convertView;
    }


    private static class ViewHolder {
        TextView txt_title;
        ImageView image_poster;
    }
}
