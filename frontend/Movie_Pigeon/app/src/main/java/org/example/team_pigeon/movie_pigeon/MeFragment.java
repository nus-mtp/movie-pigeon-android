package org.example.team_pigeon.movie_pigeon;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.example.team_pigeon.movie_pigeon.models.Movie;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import com.google.gson.stream.JsonReader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MeFragment extends Fragment {
    private final String TAG = "MePage";
    private TableRow myRatingsRow, myBookmarksRow, settingsRow, logoutRow;
    private RequestHttpBuilderSingleton requestHttpBuilder = RequestHttpBuilderSingleton.getInstance();
    private Gson gson = new Gson();
    private MyTask myTask;
    private ArrayList<Movie> movieList;
    private File credential;
    private View view;
    private Toolbar tbMe;

    public MeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, container, false);
        bindViews(view);
        myRatingsRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTask = new MyTask();
                myTask.execute("rating");
            }
        });
        myBookmarksRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTask = new MyTask();
                myTask.execute("bookmark");
            }
        });

        logoutRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Logout button pressed");
                loggingOut();
            }
        });

        settingsRow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i(TAG, "Setting Button clicked");
                Intent loadSettingActivity = new Intent(getActivity(), SettingActivity.class);
                getActivity().startActivity(loadSettingActivity);

            }
        });

        tbMe = (Toolbar) view.findViewById(R.id.me_toolbar);
        new UserInfoGetter(getContext()).execute();

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
            Toast.makeText(getContext(), "Failed to logout. Please check storage permissions.", Toast.LENGTH_SHORT).show();
        }
    }

    private void bindViews(View view){
        myBookmarksRow = (TableRow) view.findViewById(R.id.me_bookmark);
        myRatingsRow = (TableRow)view.findViewById(R.id.me_rating);
        logoutRow = (TableRow)view.findViewById(R.id.me_logout);
        settingsRow = (TableRow)view.findViewById(R.id.me_settings);
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
                    Toast.makeText(getContext(), "Connection error, please check your connection", Toast.LENGTH_SHORT).show();
                    break;

                case NO_RESULT:
                    Toast.makeText(getContext(), "Sorry, you request has no results", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }

    private class UserInfoGetter extends AsyncTask<Void, Void, Void> {
        private HttpURLConnection connection;
        private String token, serverResponse;
        private String charset = java.nio.charset.StandardCharsets.UTF_8.name();
        private boolean connectionError = false;
        private Context mContext;
        private String[] userInfo = new String[2];

        UserInfoGetter(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            token = RequestHttpBuilderSingleton.getInstance().getToken();
            request();
            return null;
        }

        private void request() {
            String url = "http://128.199.231.190:8080/api/users/";

            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setRequestProperty("Accept-Charset", charset);

                connection.connect();

                Log.i(TAG, "Finished sending to server");

                int status = connection.getResponseCode();
                Log.i(TAG, "get user info status is " + status);

                if (status == 200) {
                    InputStream response = connection.getInputStream();
                    // process the response
                    BufferedReader br = new BufferedReader(new InputStreamReader(response));
                    StringBuffer sb = new StringBuffer();
                    Log.i(TAG, "Starting to read response");
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                        serverResponse = line;
                        Log.i(TAG, "Registration Response>>>" + serverResponse);
                    }
                    try {
                        JsonReader reader = new JsonReader(new StringReader(serverResponse));
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String readStr = reader.nextName();
                            if (readStr.equals("email")) {
                                userInfo[0] = reader.nextString();
                                System.out.println(userInfo[0]);
                            } else if (readStr.equals("username")) {
                                userInfo[1] = reader.nextString();
                                System.out.println(userInfo[1]);
                            } else {
                                reader.skipValue(); //avoid some unhandled events
                            }
                        }
                        reader.endObject();
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    connectionError = true;
                    Log.i(TAG, "Error code " + status);
                }

            } catch (IOException e) {
                e.printStackTrace();
                connectionError = true;
                Log.e(TAG, "Unable to connect to server");
            }
        }

        @Override
        protected void onPostExecute(Void v) {
            if (connectionError) {
                Toast.makeText(mContext, "Unable to connect to server", Toast.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "Getting user info successful");
                tbMe.setTitle("Welcome " + userInfo[1]);
                tbMe.setSubtitle(userInfo[0]);
            }
        }
    }

}