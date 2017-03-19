package org.example.team_pigeon.movie_pigeon;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.example.team_pigeon.movie_pigeon.adapters.MovieListAdapter;
import org.example.team_pigeon.movie_pigeon.eventCenter.AddMovieToMovieListEvent;
import org.example.team_pigeon.movie_pigeon.eventCenter.DeleteMovieFromMovieListEvent;
import org.example.team_pigeon.movie_pigeon.eventCenter.UpdateMovieListEvent;
import org.example.team_pigeon.movie_pigeon.models.Movie;
import org.example.team_pigeon.movie_pigeon.models.MovieWithCount;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SHENGX on 2017/2/3.
 */

public class MovieListFragment extends Fragment implements AdapterView.OnItemClickListener{
    private static final String TAG = "MovieListFragment";
    private android.app.FragmentManager fragmentManager;
    private ArrayList<Movie> movies;
    private ListView list_movies;
    private MovieListAdapter movieListAdapter;
    private RequestHttpBuilderSingleton searchRequestHttpBuilder;
    private Gson gson = new Gson();
    public View footerView;
    public boolean isLoading = false;
    public boolean noMoreResult = false;
    private ArrayList<Movie> searchMovieList;
    private MovieWithCount movieListWithCount;
    private int resultCount;
    private Toolbar toolbar;
    private Bundle bundle;
    private String type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        //Load common views
        fragmentManager = getFragmentManager();
        View view = inflater.inflate(R.layout.fragment_movie_list,container,false);
        list_movies = (ListView)view.findViewById(R.id.list_movies);
        footerView = inflater.inflate(R.layout.footer_load_more,null);
        toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar_display_page);
        //Load pass-in content
        bundle = getActivity().getIntent().getBundleExtra("bundle");
        type = bundle.getString("type");
        movies = (ArrayList<Movie>)getArguments().getSerializable("movieList");
        movieListAdapter = new MovieListAdapter(movies,getActivity());
        list_movies.setAdapter(movieListAdapter);
        toolbar.setTitle(bundle.getString("title"));
        list_movies.setOnItemClickListener(this);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        list_movies.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),true,true));
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MoviePageFragment moviePageFragment = new MoviePageFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("movie", movieListAdapter.getItem(position));
        bundle.putInt("position", position);
        bundle.putString("type", type);
        toolbar.setTitle(movieListAdapter.getItem(position).getTitle());
        toolbar.setSubtitle(null);
        moviePageFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fl_content,moviePageFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(DeleteMovieFromMovieListEvent event){
        if(movieListAdapter!=null) {
            movieListAdapter.removeMovieItemToAdapter(event.position);
            Log.i(TAG, "A movie is removed from local list");
        }
    }

    @Subscribe
    public void onEvent(UpdateMovieListEvent event){
        if(movieListAdapter!=null) {
            movieListAdapter.updateMovieItemToAdapter(event.movie, event.position);
            Log.i(TAG, "A movie is updated to local list");
        }
    }

    @Subscribe
    public void onEvent(AddMovieToMovieListEvent event){
        if(movieListAdapter!=null) {
            movieListAdapter.addMovieItemToAdapter(event.movie, event.position);
            Log.i(TAG, "A movie is added to local list");
        }
    }


}
