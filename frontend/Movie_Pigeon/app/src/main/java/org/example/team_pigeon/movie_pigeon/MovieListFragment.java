package org.example.team_pigeon.movie_pigeon;

import android.app.Fragment;
import android.drm.DrmEvent;
import android.drm.DrmManagerClient;
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
    private SearchMoreTask searchMoreTask;
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
        if(type.equals("search")){
            toolbar.setSubtitle(bundle.getString("count"));
            list_movies.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),true,true,new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if(view.getLastVisiblePosition() == totalItemCount-1 && list_movies.getCount() >= 20 && !isLoading && !noMoreResult) {
                        isLoading = true;
                        searchMoreTask = new SearchMoreTask();
                        searchMoreTask.execute();
                    }
                }
            }));
        }
        else{
            list_movies.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),true,true));
        }
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
    public void onEvent(AddMovieToMovieListEvent event){
        movieListAdapter.addMovieItemToAdapter(event.movie, event.position);
        Log.i(TAG, "New movie is added to local list");
    }

    @Subscribe
    public void onEvent(DeleteMovieFromMovieListEvent event){
        movieListAdapter.removeMovieItemToAdapter(event.position);
        Log.i(TAG, "A movie is removed from local list");
    }

    @Subscribe
    public void onEvent(UpdateMovieListEvent event){
        movieListAdapter.updateMovieItemToAdapter(event.movie, event.position);
        Log.i(TAG, "A movie is updated to local list");
    }

    //Async thread to handle search request
    private class SearchMoreTask extends AsyncTask<Void, Integer, Void> {
        private final int SUCCESSFUL = 0;
        private final int ERROR = 1;
        private final int NO_RESULT = 2;
        private final int NO_INTERNET = 3;
        private final int NO_MORE_RESULT = 4;
        int status;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                searchRequestHttpBuilder = RequestHttpBuilderSingleton.getInstance();
                OkHttpClient client = searchRequestHttpBuilder.getClient();
                Request request = searchRequestHttpBuilder.getNextSearchRequest();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Log.i(TAG, "connect failed");
                    status = ERROR;
                    throw new IOException("Unexpected code" + response);
                }
                //Convert json to Arraylist<Movie>
                movieListWithCount = gson.fromJson(response.body().charStream(), new TypeToken<MovieWithCount>() {}.getType());
                resultCount = movieListWithCount.getCount();
                searchMovieList = movieListWithCount.getMovies();

                if (resultCount == 0) {
                    status = NO_RESULT;
                }
                else if(searchMovieList.size() < searchRequestHttpBuilder.getLimit()){
                    status = NO_MORE_RESULT;
                }
                else {
                    status = SUCCESSFUL;
                }
                return null;
            } catch (IOException e) {
                status = NO_INTERNET;
                Log.e(TAG, e.getMessage());
            }
            return null;

        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "New search request is initialised");
            list_movies.addFooterView(footerView);
            return;
        }

        @Override
        protected void onPostExecute(Void params) {
            switch (status) {
                case SUCCESSFUL:
                    Log.i(TAG, "Search is completed");
                    list_movies.removeFooterView(footerView);
                    movieListAdapter.addListItemToAdapter(searchMovieList);
                    isLoading = false;
                    break;

                case ERROR:
                    Toast.makeText(getContext(), "Connection error, please check your connection", Toast.LENGTH_SHORT).show();
                    list_movies.removeFooterView(footerView);
                    isLoading = false;
                    break;

                case NO_RESULT:
                    Toast.makeText(getContext(), "Sorry, there is no more result", Toast.LENGTH_SHORT).show();
                    list_movies.removeFooterView(footerView);
                    noMoreResult = true;
                    isLoading = false;
                    break;

                case NO_MORE_RESULT:
                    list_movies.removeFooterView(footerView);
                    noMoreResult = true;
                    isLoading = false;
                    movieListAdapter.addListItemToAdapter(searchMovieList);
                    break;

                case NO_INTERNET:
                    Toast.makeText(getContext(), "Connection error, please make sure that you have Internet connection.", Toast.LENGTH_SHORT).show();
                    list_movies.removeFooterView(footerView);
                    isLoading = false;
                    break;
            }

        }
    }
}
