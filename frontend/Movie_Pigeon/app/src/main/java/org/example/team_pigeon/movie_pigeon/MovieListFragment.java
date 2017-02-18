package org.example.team_pigeon.movie_pigeon;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import org.example.team_pigeon.movie_pigeon.adapters.MovieListAdapter;
import org.example.team_pigeon.movie_pigeon.models.Movie;

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
    private ListRequestHttpBuilderSingleton searchRequestHttpBuilder;
    private Gson gson = new Gson();
    public View footerView;
    public boolean isLoading = false;
    public boolean noMoreResult = false;
    private ArrayList<Movie> searchMovieList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        fragmentManager = getFragmentManager();
        movies = (ArrayList<Movie>)getArguments().getSerializable("searchMovieList");
        View view = inflater.inflate(R.layout.fragment_movie_list,container,false);
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar_display_page);
        toolbar.setTitle(getActivity().getIntent().getBundleExtra("bundle").getString("title"));
        list_movies = (ListView)view.findViewById(R.id.list_movies);
        footerView = inflater.inflate(R.layout.footer_load_more,null);
        movieListAdapter = new MovieListAdapter(movies,getActivity());
        list_movies.setAdapter(movieListAdapter);

        //Scrolling to load more function will be implement in the next version
        list_movies.setOnScrollListener(new AbsListView.OnScrollListener() {
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
        });
        list_movies.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MoviePageFragment moviePageFragment = new MoviePageFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("movie", movieListAdapter.getItem(position));
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar_display_page);
        toolbar.setTitle(movieListAdapter.getItem(position).getTitle());
        moviePageFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fl_content,moviePageFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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
                searchRequestHttpBuilder = ListRequestHttpBuilderSingleton.getInstance();
                OkHttpClient client = searchRequestHttpBuilder.getClient();
                Request request = searchRequestHttpBuilder.getNextSearchRequest();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Log.i(TAG, "connect failed");
                    status = ERROR;
                    throw new IOException("Unexpected code" + response);
                }
                //Convert json to Arraylist<Movie>
                searchMovieList = gson.fromJson(response.body().charStream(), new TypeToken<ArrayList<Movie>>() {
                }.getType());
                Log.i(TAG,String.valueOf(searchMovieList.size()));

                if (searchMovieList.size() == 0) {
                    status = NO_RESULT;
                } else if(searchMovieList.size() < searchRequestHttpBuilder.getLimit()){
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
            }

        }
    }
}
