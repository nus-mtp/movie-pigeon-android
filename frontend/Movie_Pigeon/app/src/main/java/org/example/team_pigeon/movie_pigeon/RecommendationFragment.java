package org.example.team_pigeon.movie_pigeon;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.example.team_pigeon.movie_pigeon.models.Movie;
import org.example.team_pigeon.movie_pigeon.models.MovieWithCount;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecommendationFragment extends Fragment {
    private static final String TAG = "RecommendationFragment";
    private SearchView searchView = null;
    private Gson gson = new Gson();
    private SearchTask searchTask;
    private Bundle arguments;
    private ArrayList<Movie> searchMovieList;
    private ArrayList<Movie> topMovieList;
    private ArrayList<Movie> recommendedMovieList;
    private MovieWithCount movieWithCount;
    private int resultCount = 0;
    private RequestHttpBuilderSingleton searchRequestHttpBuilder = RequestHttpBuilderSingleton.getInstance();

    public RecommendationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommendation, container, false);
        searchView = (SearchView) view.findViewById(R.id.search_view);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //Start to search movie if there is query present and submit button is pressed
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "Search query submit = " + query);
                searchTask = new SearchTask();
                searchTask.execute(query);
                return false;
            }
            //TODO: Search suggestion
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return view;
    }

    //Async thread to handle search request
    private class SearchTask extends AsyncTask<String, Integer, Void> {
        private final int SUCCESSFUL = 0;
        private final int ERROR = 1;
        private final int NO_RESULT = 2;
        private final int NO_INTERNET = 3;
        int status;
        String keyword = null;

        @Override
        protected Void doInBackground(String... params) {
            try {
                keyword = params[0];
                searchRequestHttpBuilder.setKeywords(keyword);
                OkHttpClient client = searchRequestHttpBuilder.getClient();
                Request request = searchRequestHttpBuilder.getSearchRequest();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Log.i(TAG, "connect failed");
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
            return;
        }

        @Override
        protected void onPostExecute(Void params) {
            switch (status) {
                case SUCCESSFUL:
                    Log.i(TAG, "Search is completed");
                    Intent displayActivityIntent = new Intent(getActivity(), DisplayActivity.class);
                    arguments = new Bundle();
                    arguments.putSerializable("movieList", searchMovieList);
                    arguments.putString("type","search");
                    arguments.putString("title", "Result of '" + searchRequestHttpBuilder.getKeywords() + "'");
                    arguments.putString("count", "Found "+String.valueOf(resultCount)+ " movies");
                    displayActivityIntent.putExtra("bundle", arguments);
                    searchView.setQuery("",false);
                    searchView.clearFocus();
                    getActivity().startActivity(displayActivityIntent);
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
