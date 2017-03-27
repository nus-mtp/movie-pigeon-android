package org.example.team_pigeon.movie_pigeon;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.example.team_pigeon.movie_pigeon.adapters.HorizontalListAdapter;
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

public class RecommendationFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "RecommendationFragment";
    private SearchView searchView = null;
    private Gson gson = new Gson();
    private Task nowShowingTask,recommendationTask,searchTask;
    private Bundle arguments;
    private ArrayList<Movie> searchMovieList, nowShowingMovieList, recommendedMovieList, filteredNowShowingList;
    private HorizontalListAdapter nowShowingMovieAdapter, recommendedMovieAdapter;
    private MovieWithCount movieWithCount;
    private GridView nowShowingGrid, recommendedGrid;
    private int resultCount = 0;
    private RequestHttpBuilderSingleton searchRequestHttpBuilder = RequestHttpBuilderSingleton.getInstance();
    private LoadingDialog loadingDialog;

    public RecommendationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadingDialog = new LoadingDialog(this.getActivity(),R.style.LoadingDialog);
        View view = inflater.inflate(R.layout.fragment_recommendation, container, false);
        searchView = (SearchView) view.findViewById(R.id.search_view);
        nowShowingGrid = (GridView) view.findViewById(R.id.grid_now_showing);
        recommendedGrid = (GridView) view.findViewById(R.id.grid_recommended);
        nowShowingMovieList = new ArrayList<>();
        nowShowingTask = new Task();
        nowShowingTask.execute("nowshowing");
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //Start to search movie if there is query present and submit button is pressed
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "Search query submit = " + query);
                //Check if the characters are all ASCII characters
                if(query.matches("^\\p{ASCII}*$")){
                    filteredNowShowingList = filterNowShowing(nowShowingMovieList,query);
                    searchTask = new Task();
                    searchTask.execute("search",query);
                }
                else{
                    Toast.makeText(getActivity(),"Please use English alphabets to search.",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
            //TODO: Search suggestion
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent displayActivityIntent = new Intent(getActivity(), DisplayActivity.class);
        Bundle arguments = new Bundle();
        switch (parent.getId()) {
            case R.id.grid_now_showing:
                arguments.putSerializable("movie", nowShowingMovieAdapter.getItem(position));
                arguments.putString("title", nowShowingMovieAdapter.getItem(position).getTitle());
                break;
            case R.id.grid_recommended:
                arguments.putSerializable("movie", recommendedMovieAdapter.getItem(position));
                arguments.putString("title", recommendedMovieAdapter.getItem(position).getTitle());
                break;
        }
        arguments.putString("type", "moviePage");
        arguments.putInt("position",position);
        displayActivityIntent.putExtra("bundle", arguments);
        getActivity().startActivity(displayActivityIntent);
    }

    private void setGridView(GridView gridView, int size){
        int length = 100;
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (size*length*density),LinearLayout.LayoutParams.FILL_PARENT);
        gridView.setLayoutParams(params);
        gridView.setColumnWidth((int) (length*density));
        gridView.setHorizontalSpacing(0);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size);
    }

    private ArrayList<Movie> filterNowShowing(ArrayList<Movie> allNowShowing, String keywords){
        ArrayList<Movie> filteredMovies = new ArrayList<>();
        keywords = keywords.toLowerCase();
        for(Movie movie:allNowShowing){
            if(movie.getTitle().toLowerCase().contains(keywords)){
                filteredMovies.add(movie);
            }
        }
        return filteredMovies;
    }

    @Subscribe
    public void onEvent(UpdateMovieListEvent event){
        if(nowShowingMovieAdapter!=null) {
            if(event.movie.getMovieID().equals(nowShowingMovieAdapter.getItem(event.position).getMovieID())) {
                nowShowingMovieAdapter.updateMovieItemToAdapter(event.movie, event.position);
                Log.i(TAG, "A movie is updated to local list");
            }
        }
        if(recommendedMovieAdapter!=null) {
            if(event.movie.getMovieID().equals(recommendedMovieAdapter.getItem(event.position).getMovieID())) {
                recommendedMovieAdapter.updateMovieItemToAdapter(event.movie, event.position);
                Log.i(TAG, "A movie is updated to local list");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void setOnclickListener(GridView gridView){
        gridView.setOnItemClickListener(this);
    }

    //Async thread to handle search request
    private class Task extends AsyncTask<String, Integer, Void> {
        private final int SUCCESSFUL_SEARCH = 0;
        private final int SUCCESSFUL_NOW_SHOWING = 4;
        private final int SUCCESSFUL_RECOMMENDATION = 6;
        private final int ERROR = 1;
        private final int NO_RESULT = 2;
        private final int NO_RECOMMENDATION = 7;
        private final int NO_INTERNET = 3;
        private final int NO_NOW_SHOWING = 5;
        int status;
        String type;
        String keyword = null;

        @Override
        protected Void doInBackground(String... params) {
            type = params[0];
            switch (type){
                case "search":
                    try {
                        keyword = params[1];
                        searchRequestHttpBuilder.setKeywords(keyword);
                        OkHttpClient client = searchRequestHttpBuilder.getClient();
                        Request request = searchRequestHttpBuilder.getSearchRequest();
                        Response response = client.newCall(request).execute();
                        if (!response.isSuccessful()) {
                            Log.i(TAG, "Search connection failed");
                            status = ERROR;
                            throw new IOException("Unexpected code" + response);
                        }
                        //Convert json to Arraylist<Movie>
                        movieWithCount = gson.fromJson(response.body().charStream(), new TypeToken<MovieWithCount>() {
                        }.getType());
                        searchMovieList = movieWithCount.getMovies();
                        resultCount = movieWithCount.getCount();

                        if (resultCount == 0) {
                            status = NO_RESULT;
                        } else {
                            status = SUCCESSFUL_SEARCH;
                        }
                        return null;
                    } catch (IOException e) {
                        status = NO_INTERNET;
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                case "nowshowing":
                    try {
                        OkHttpClient client = searchRequestHttpBuilder.getClient();
                        Request request = searchRequestHttpBuilder.getNowShowingListRequest();
                        Response response = client.newCall(request).execute();
                        if(!response.isSuccessful()){
                            Log.i(TAG,"Now showing connection failed");
                            status = ERROR;
                            throw new IOException("Unexpected code"+response);
                        }
                        nowShowingMovieList = gson.fromJson(response.body().charStream(), new TypeToken<ArrayList<Movie>>() {}.getType());
                        if(nowShowingMovieList.isEmpty()){
                            status = NO_NOW_SHOWING;
                        }
                        else{
                            status = SUCCESSFUL_NOW_SHOWING;
                            for(Movie movie:nowShowingMovieList){
                                movie.setShowing(true);
                            }
                        }
                        return null;
                    }
                    catch (IOException e) {
                        status = NO_INTERNET;
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                case "recommendation":
                    try {
                        OkHttpClient client = searchRequestHttpBuilder.getClient();
                        Request request = searchRequestHttpBuilder.getRecommendationRequest();
                        Response response = client.newCall(request).execute();
                        if(!response.isSuccessful()){
                            Log.i(TAG,"Recommendation connection failed");
                            status = ERROR;
                            throw new IOException("Unexpected code"+response);
                        }
                        recommendedMovieList = gson.fromJson(response.body().charStream(), new TypeToken<ArrayList<Movie>>() {}.getType());
                        if(recommendedMovieList.isEmpty()){
                            status = NO_RECOMMENDATION;
                        }
                        else{
                            status = SUCCESSFUL_RECOMMENDATION;
                        }
                        return null;
                    }
                    catch (IOException e) {
                        status = NO_INTERNET;
                        Log.e(TAG, e.getMessage());
                    }
            }
            return null;

        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "Async request is initialised");
            loadingDialog.show();
            return;
        }

        @Override
        protected void onPostExecute(Void params) {
            loadingDialog.dismiss();
            switch (status) {
                case SUCCESSFUL_SEARCH:
                    Log.i(TAG, "Search is completed");
                    Intent displayActivityIntent = new Intent(getActivity(), DisplayActivity.class);
                    arguments = new Bundle();
                    arguments.putSerializable("nowShowingList",filteredNowShowingList);
                    arguments.putSerializable("allList", searchMovieList);
                    arguments.putString("type","search");
                    arguments.putString("title", "Result of '" + searchRequestHttpBuilder.getKeywords() + "'");
                    arguments.putString("count", "Found "+String.valueOf(resultCount)+ " movies");
                    displayActivityIntent.putExtra("bundle", arguments);
                    searchView.setQuery("",false);
                    searchView.clearFocus();
                    getActivity().startActivity(displayActivityIntent);
                    break;
                case SUCCESSFUL_NOW_SHOWING:
                    Log.i(TAG,"Get now showing list successfully");
                    //After get now showing list, get recommendation
                    recommendationTask = new Task();
                    recommendationTask.execute("recommendation");
                    setGridView(nowShowingGrid,nowShowingMovieList.size());
                    nowShowingMovieAdapter = new HorizontalListAdapter(getActivity(),nowShowingMovieList);
                    nowShowingGrid.setAdapter(nowShowingMovieAdapter);
                    setOnclickListener(nowShowingGrid);
                    nowShowingMovieAdapter.notifyDataSetChanged();
                    break;
                case SUCCESSFUL_RECOMMENDATION:
                    Log.i(TAG,"Get recommendation list successfully");
                    setGridView(recommendedGrid,recommendedMovieList.size());
                    recommendedMovieAdapter = new HorizontalListAdapter(getActivity(),recommendedMovieList);
                    recommendedGrid.setAdapter(recommendedMovieAdapter);
                    setOnclickListener(recommendedGrid);
                    recommendedMovieAdapter.notifyDataSetChanged();
                    break;
                case ERROR:
                    Toast.makeText(getActivity(), "Connection error, please check your connection", Toast.LENGTH_SHORT).show();
                    break;

                case NO_RESULT:
                    Toast.makeText(getActivity(), "Sorry, the search has no results", Toast.LENGTH_SHORT).show();
                    break;

                case NO_INTERNET:
                    Toast.makeText(getActivity(), "Connection error, please make sure that you have Internet connection.", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }
}
