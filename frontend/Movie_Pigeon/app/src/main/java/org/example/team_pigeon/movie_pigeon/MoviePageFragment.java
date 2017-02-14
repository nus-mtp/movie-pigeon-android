package org.example.team_pigeon.movie_pigeon;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by SHENGX on 2017/2/4.
 */

public class MoviePageFragment extends Fragment {
    private TextView txt_title,txt_country,txt_length,txt_director,txt_plot,txt_genres,txt_year,txt_actors;
    private TableRow row_title,row_country,row_length,row_director,row_genres,row_year,row_actors;
    private ImageView image_poster;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_page,container,false);
        loadViews(view);
        setContent(getArguments());
        return view;
    }

    private void loadViews(View view) {
        txt_title = (TextView) view.findViewById(R.id.text_movie_page_title_content);
        txt_country = (TextView) view.findViewById(R.id.text_movie_page_country_content);
        txt_director = (TextView) view.findViewById(R.id.text_movie_page_director_content);
        txt_plot = (TextView) view.findViewById(R.id.text_movie_page_plot_content);
        txt_length = (TextView) view.findViewById(R.id.text_movie_page_length_content);
        txt_genres = (TextView) view.findViewById(R.id.text_movie_page_genres_content);
        txt_year = (TextView) view.findViewById(R.id.text_movie_page_year_content);
        txt_actors = (TextView) view.findViewById(R.id.text_movie_page_actors_content);
        image_poster = (ImageView) view.findViewById(R.id.image_page_poster);
        row_title = (TableRow) view.findViewById(R.id.row_title);
        row_year = (TableRow) view.findViewById(R.id.row_year);
        row_country = (TableRow) view.findViewById(R.id.row_country);
        row_director = (TableRow) view.findViewById(R.id.row_director);
        row_length = (TableRow) view.findViewById(R.id.row_length);
        row_genres = (TableRow) view.findViewById(R.id.row_genres);
        row_actors = (TableRow) view.findViewById(R.id.row_actors);
    }

    private void setContent(Bundle argument){
        txt_title.setText(argument.getString("title"));
        if(argument.getString("poster") != null){
            ImageLoader.getInstance().displayImage(argument.getString("poster"),image_poster);
        }
        else{
            image_poster.setImageResource(R.mipmap.image_no_poster_found);
        }
        if(argument.getString("plot") != null){
            txt_plot.setText(argument.getString("plot"));
        }
        setTextOtherwiseSetGone(argument,"year",row_year,txt_year);
        setTextOtherwiseSetGone(argument,"country",row_country,txt_country);
        setTextOtherwiseSetGone(argument,"length",row_length,txt_length);
        setTextOtherwiseSetGone(argument,"director",row_director,txt_director);
        setTextOtherwiseSetGone(argument,"genres",row_genres,txt_genres);
        setTextOtherwiseSetGone(argument,"actors",row_actors,txt_actors);

    }

    private void setTextOtherwiseSetGone(Bundle argument, String name,TableRow row, TextView text){
        if(argument.getString(name)!= null) {
            if(name.equals("length")){
                text.setText(argument.getString(name) + "min");
            }
            else {
                text.setText(argument.getString(name));
            }
        }
        else{
            row.setVisibility(View.GONE);
        }
    }
}

