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
import org.example.team_pigeon.movie_pigeon.eventCenter.UpdateMovieListEvent;
import org.example.team_pigeon.movie_pigeon.models.Movie;
import org.example.team_pigeon.movie_pigeon.models.MovieWithCount;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SHENGX on 2017/2/5.
 */

public class SearchPageFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "SearchPageFragment";
    private android.app.FragmentManager fragmentManager;
    private ListView movieListView;
    private SearchMoreTask searchMoreTask;
    private MovieListAdapter movieListAdapter;
    private RequestHttpBuilderSingleton searchRequestHttpBuilder;
    private Gson gson = new Gson();
    private View nowShowingHeaderView;
    private View footerView;
    public boolean isLoading = false;
    public boolean noMoreResult = false;
    private ArrayList<Movie> allMovieList, nowShowingMovieList, moreMovieList, displayMovieList;
    private MovieWithCount movieListWithCount;
    private int resultCount;
    private Toolbar toolbar;
    private Bundle bundle;
    private int nowShowingCount;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        //Load pass-in content
        displayMovieList = new ArrayList<>();
        allMovieList = (ArrayList<Movie>) getArguments().getSerializable("allList");
        nowShowingMovieList = (ArrayList<Movie>) getArguments().getSerializable("nowShowingList");
        nowShowingCount = nowShowingMovieList.size();
        if(nowShowingCount==0){
            movieListAdapter = new MovieListAdapter(allMovieList, getActivity());
        }
        else {
            movieListAdapter = new MovieListAdapter(displayMovieList,getActivity());
            movieListAdapter.addListItemToAdapter(nowShowingMovieList);
            movieListAdapter.addHeaderToAdapter(new Movie("***HEADER***","***HEADER***"));
            movieListAdapter.addListItemToAdapter(allMovieList);
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        //Load common views
        fragmentManager = getFragmentManager();
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        footerView = inflater.inflate(R.layout.footer_load_more, null);
        nowShowingHeaderView = inflater.inflate(R.layout.header_now_showing_list,null);
        bindView(view);
        bundle = getActivity().getIntent().getBundleExtra("bundle");

        if(nowShowingCount!=0){
            movieListView.addHeaderView(nowShowingHeaderView,null,false);
        }

        movieListView.setOnItemClickListener(this);
        movieListView.setAdapter(movieListAdapter);

        toolbar.setTitle(bundle.getString("title"));
        toolbar.setSubtitle(bundle.getString("count"));
        movieListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int movieCountThreshold = nowShowingCount==0? 20 : 21+nowShowingCount;
                if (view.getLastVisiblePosition() == totalItemCount-1 && movieListView.getCount() >= movieCountThreshold && !isLoading && !noMoreResult) {
                    isLoading = true;
                    searchMoreTask = new SearchMoreTask();
                    searchMoreTask.execute();
                }
            }
        }));
        return view;
    }

    private void bindView(View view) {
        //allMovieListView = (ListView) view.findViewById(R.id.all_in_search);
        //nowShowingListView = (ListView) view.findViewById(R.id.now_showing_in_search);
        movieListView = (ListView) view.findViewById(R.id.list_movies);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_display_page);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        boolean needSkip = !(nowShowingCount==0);
        if(needSkip && position == nowShowingCount+1) {}
        else{
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MoviePageFragment moviePageFragment = new MoviePageFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("movie", (Serializable) parent.getAdapter().getItem(position));
            bundle.putInt("position", position);
            bundle.putString("type", "search");
            toolbar.setTitle(((Movie) parent.getAdapter().getItem(position)).getTitle());
            toolbar.setSubtitle(null);
            moviePageFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.fl_content, moviePageFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    @Subscribe
    public void onEvent(UpdateMovieListEvent event) {
        if(movieListAdapter!=null) {
            //position =- -1 when there is a header
            if(nowShowingCount==0){
                movieListAdapter.updateMovieItemToAdapter(event.movie, event.position);
            }
            else {
                movieListAdapter.updateMovieItemToAdapter(event.movie, event.position - 1);
            }
            Log.i(TAG, "A movie is updated to local list");
        }
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
                movieListWithCount = gson.fromJson(response.body().charStream(), new TypeToken<MovieWithCount>() {
                }.getType());
                resultCount = movieListWithCount.getCount();
                moreMovieList = movieListWithCount.getMovies();

                if (resultCount == 0) {
                    status = NO_RESULT;
                } else if (moreMovieList.size() < searchRequestHttpBuilder.getLimit()) {
                    status = NO_MORE_RESULT;
                } else {
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
            movieListView.addFooterView(footerView,null,false);
            return;
        }

        @Override
        protected void onPostExecute(Void params) {
           movieListView.removeFooterView(footerView);
            switch (status) {
                case SUCCESSFUL:
                    Log.i(TAG, "Search is completed");
                    movieListAdapter.addListItemToAdapter(moreMovieList);
                    isLoading = false;
                    break;

                case ERROR:
                    Toast.makeText(getActivity(), "Connection error, please check your connection", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                    break;

                case NO_RESULT:
                    Toast.makeText(getActivity(), "Sorry, there is no more result", Toast.LENGTH_SHORT).show();
                    noMoreResult = true;
                    isLoading = false;
                    break;

                case NO_MORE_RESULT:
                    noMoreResult = true;
                    isLoading = false;
                    movieListAdapter.addListItemToAdapter(moreMovieList);
                    break;

                case NO_INTERNET:
                    Toast.makeText(getActivity(), "Connection error, please make sure that you have Internet connection.", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                    break;
            }

        }
    }
}
