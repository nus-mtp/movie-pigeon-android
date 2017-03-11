package org.example.team_pigeon.movie_pigeon;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.example.team_pigeon.movie_pigeon.models.Movie;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MeFragment extends Fragment {
    private final String TAG = "MePage";
    private Button myRatingsButton, myBookmarksButton, settingButton, logoutButton;
    private RequestHttpBuilderSingleton requestHttpBuilder = RequestHttpBuilderSingleton.getInstance();
    private Gson gson = new Gson();
    private MyTask myTask;
    private ArrayList<Movie> movieList;
    private File credential;
    private View view;

    public MeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, container, false);
        bindViews(view);
        myRatingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTask = new MyTask();
                myTask.execute("rating");
            }
        });
        myBookmarksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTask = new MyTask();
                myTask.execute("bookmark");
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Logout button pressed");
                loggingOut();
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i(TAG, "Setting Button clicked");
                Intent loadSettingActivity = new Intent(getActivity(), SettingActivity.class);
                getActivity().startActivity(loadSettingActivity);

            }
        });

        return view;
    }

    private void loggingOut() {
        Intent loadStartActivity = new Intent(getActivity(), StartActivity.class);
        credential = new File(Environment.getExternalStorageDirectory() + "/MoviePigeon/Signin/credential");
        if (credential.exists()) {
            Log.i(TAG, "Deleting existing credentials");
            credential.delete();
        }

        if (!credential.exists()) {
            Log.i(TAG, "Starting sign in activity");
            getActivity().startActivity(loadStartActivity);
            Log.i(TAG, "Finishing current activity");
            getActivity().finish();
        } else {
            Log.e(TAG, "Failed to delete credential");
            Toast.makeText(getActivity(), "Failed to logout. Please check storage permissions.", Toast.LENGTH_SHORT).show();
        }
    }

    private void bindViews(View view){
        myBookmarksButton = (Button)view.findViewById(R.id.button_my_bookmarks);
        myRatingsButton = (Button)view.findViewById(R.id.button_my_rating);
        logoutButton = (Button)view.findViewById(R.id.button_logout);
        settingButton = (Button)view.findViewById(R.id.button_setting);
    }

    //Async thread to handle myBookmarks and myRatings request
    private class MyTask extends AsyncTask<String, Integer, Integer> {
        private int successfulStatus = -1;
        private final int SUCCESSFUL_BOOKMARK_LIST = 0;
        private final int SUCCESSFUL_RATING_LIST = 1;
        private final int NO_RESULT = 2;
        private final int ERROR = 3;
        Request request;

        @Override
        protected Integer doInBackground(String... params) {
            try {
                OkHttpClient client = requestHttpBuilder.getClient();
                if(params[0].equals("rating")){
                    request = requestHttpBuilder.getRatingListRequest();
                    successfulStatus = SUCCESSFUL_RATING_LIST;
                }
                if(params[0].equals("bookmark")){
                    request = requestHttpBuilder.getBookmarkListRequest();
                    successfulStatus = SUCCESSFUL_BOOKMARK_LIST;
                }
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Log.i(TAG, "Request failed");
                    throw new IOException("Unexpected code" + response);
                }
                //Convert json to ArrayList<Movie>
                movieList = gson.fromJson(response.body().charStream(), new TypeToken<ArrayList<Movie>>() {
                }.getType());

                if (movieList.isEmpty()) {
                    return NO_RESULT;
                }else{
                    return successfulStatus;
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return ERROR;
            }
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "New search request is initialised");
            return;
        }

        @Override
        protected void onPostExecute(Integer status) {
            Intent displayActivityIntent = new Intent(getActivity(), DisplayActivity.class);
            Bundle arguments = new Bundle();
            switch (status) {
                case SUCCESSFUL_BOOKMARK_LIST:
                    Log.i(TAG, "Request of bookmark list is completed");
                    arguments.putSerializable("movieList",movieList);
                    arguments.putString("type", "bookmark");
                    arguments.putString("title", "My Bookmarks");
                    displayActivityIntent.putExtra("bundle", arguments);
                    getActivity().startActivity(displayActivityIntent);
                    break;

                case SUCCESSFUL_RATING_LIST:
                    Log.i(TAG, "Request of bookmark list is completed");
                    arguments.putSerializable("movieList",movieList);
                    arguments.putString("type", "rating");
                    arguments.putString("title", "My Rating History");
                    displayActivityIntent.putExtra("bundle", arguments);
                    getActivity().startActivity(displayActivityIntent);
                    break;

                case ERROR:
                    Toast.makeText(getActivity(), "Connection error, please check your connection", Toast.LENGTH_SHORT).show();
                    break;

                case NO_RESULT:
                    Toast.makeText(getActivity(), "Sorry, you request has no results", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }

}