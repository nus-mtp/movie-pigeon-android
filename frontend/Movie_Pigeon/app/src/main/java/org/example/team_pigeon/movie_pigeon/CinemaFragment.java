package org.example.team_pigeon.movie_pigeon;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.example.team_pigeon.movie_pigeon.adapters.CinemaAdapter;
import org.example.team_pigeon.movie_pigeon.adapters.CinemaListAdapter;
import org.example.team_pigeon.movie_pigeon.adapters.NowShowingListAdapter;
import org.example.team_pigeon.movie_pigeon.eventCenter.AddMovieToMovieListEvent;
import org.example.team_pigeon.movie_pigeon.eventCenter.DeleteMovieFromMovieListEvent;
import org.example.team_pigeon.movie_pigeon.eventCenter.UpdateMovieListEvent;
import org.example.team_pigeon.movie_pigeon.models.Cinema;
import org.example.team_pigeon.movie_pigeon.models.Movie;
import org.example.team_pigeon.movie_pigeon.models.Schedule;
import org.example.team_pigeon.movie_pigeon.utils.TimeUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CinemaFragment extends Fragment {
    private static final String TAG = "CinemaFragment";
    private static final String GET_CINEMAS = "1";
    private static final String GET_MOVIES = "2";
    private Gson gson = new Gson();
    private RequestHttpBuilderSingleton httpRequestBuilder = RequestHttpBuilderSingleton.getInstance();
    private ArrayList<Cinema> cinemas = new ArrayList<>();
    private ArrayList<Cinema> allCinemas;
    private ArrayList<ArrayList<Movie>> oneWeekMovieList;
    private ArrayList<Movie> movieList;
    private ArrayList<Movie> moviesOfTheDay = new ArrayList<>();
    private NowShowingTask nowShowingTask;
    private List<String> dateListInString;
    private List<Date> dateList;
    private CinemaAdapter cinemaAdapter = null;
    private NowShowingListAdapter nowShowingListAdapter = null;
    private boolean isCinemasLoaded = false;
    private int currentDay;
    private TimeUtil timeUtil = new TimeUtil();
    private GlobalReceiver receiver;
    private final int cinemasLoaded = 1;
    private final int locationLoaded = 0;
    private CinemaListAdapter cinemaListAdapter;
    private ListView cinemaList;
    private boolean isGpsEnabled = false;
    Location userLocation;
    private String selectedCinemaName;

    public CinemaFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cinema_new, container, false);
        dateList = timeUtil.getDateList();
        dateListInString = timeUtil.getDateListToString_MMDDE(dateList);
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = getContext().checkCallingOrSelfPermission(permission);
        isGpsEnabled = (res == PackageManager.PERMISSION_GRANTED);
        if (!isGpsEnabled) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Log.i(TAG, "onCreateView: asking for gps permission");
        }
        cinemaList = (ListView) view.findViewById(R.id.cinema_list);
        receiver = new GlobalReceiver(new Handler() {
            public void handleMessage(Message msg) {
                final int what = msg.what;
                switch (what) {
                    case cinemasLoaded:
                        Log.i(TAG, "Successfully loaded cinemas");
                        showCinemas();
                        break;
                    case locationLoaded:
                        Log.i(TAG, "Successfully loaded location");
                        Bundle coordinates = msg.getData();
                        userLocation = new Location("user");
                        userLocation.setLatitude(coordinates.getDouble("lat"));
                        userLocation.setLongitude(coordinates.getDouble("lon"));
                        Log.i(TAG, "User location is " + userLocation);
                        loadCinemaList();
                        break;
                }
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction("cinemasLoaded");
        filter.addAction("locationLoaded");
        getContext().registerReceiver(receiver, filter);
        view.requestFocus();
        return view;
    }

    private void loadCinemaList() {
        allCinemas = new ArrayList<>();
        nowShowingTask = new NowShowingTask();
        nowShowingTask.execute(GET_CINEMAS);
    }

    private void showCinemas() {
        Log.i(TAG, "Preparing to generate cinema table");
        // sort cinemas according to distance
        Collections.sort(allCinemas,
                new Comparator<Cinema>() {
                    @Override
                    public int compare(Cinema o1, Cinema o2) {
                        return o1.getDistance() - o2.getDistance();
                    }
                });
        // load table rows containing cinema name and distance
        cinemaListAdapter = new CinemaListAdapter(allCinemas, getContext());
        cinemaList.setAdapter(cinemaListAdapter);
        cinemaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: position is " + position);
                String cinemaId = allCinemas.get(position).getId();
                selectedCinemaName = allCinemas.get(position).getName();
                Log.i(TAG, "onItemClick: " + selectedCinemaName);
                Intent displayMovies = new Intent(getActivity(), DisplayActivity.class);
                Bundle arguments = new Bundle();
                Log.i(TAG, "Request of now showing movie list is completed");
                arguments.putString("cinemaId", cinemaId);
                arguments.putString("type", "nowShowing");
                arguments.putString("title", selectedCinemaName);
                displayMovies.putExtra("bundle", arguments);
                getActivity().startActivity(displayMovies);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (isGpsEnabled) {
            getContext().unregisterReceiver(receiver);
        }
    }

    private class NowShowingTask extends AsyncTask<String, Integer, Void> {
        private final int SUCCESSFUL_CINEMALIST = 0;
        private final int ERROR = 2;
        private final int NO_RESULT = 3;
        private final int NO_INTERNET = 4;
        private String requestType;
        private Location cinemaLocation;
        int status;

        @Override
        protected Void doInBackground(String... params) {
            requestType = params[0];
            if (requestType.equals(GET_CINEMAS)) {
                getCinemas();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            return;
        }

        @Override
        protected void onPostExecute(Void params) {
            switch (status) {
                case SUCCESSFUL_CINEMALIST:
                    Log.i(TAG, "Requset is completed");
                    isCinemasLoaded = true;
                    Intent intent = new Intent("cinemasLoaded");
                    getContext().sendBroadcast(intent);
                    break;
                case ERROR:
                    Toast.makeText(getActivity(), "Connection error, please check your connection", Toast.LENGTH_SHORT).show();
                    break;

                case NO_RESULT:
                    Toast.makeText(getActivity(), "Sorry, there is no results", Toast.LENGTH_SHORT).show();
                    break;

                case NO_INTERNET:
                    Toast.makeText(getActivity(), "Connection error, please make sure that you have Internet connection.", Toast.LENGTH_SHORT).show();
                    break;
            }

        }

        protected void getCinemas() {
            OkHttpClient client = httpRequestBuilder.getClient();
            Request request = httpRequestBuilder.getCinemasRequest();
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Log.i(TAG, "connect failed");
                    status = ERROR;
                    throw new IOException("Unexpected code" + response);
                }
                //Convert json to Arraylist<Cinema>
                cinemas = gson.fromJson(response.body().charStream(), new TypeToken<ArrayList<Cinema>>() {}.getType());
                //Sort the cinema list into list of different brands
                if (cinemas.size() == 0) {
                    status = NO_RESULT;
                } else {
                    status = SUCCESSFUL_CINEMALIST;
                    for (Cinema cinema : cinemas) {
                        if (userLocation != null) {
                            cinemaLocation = new Location(cinema.getName());
                            cinemaLocation.setLatitude(Double.valueOf(cinema.getLatitude()));
                            cinemaLocation.setLongitude(Double.valueOf(cinema.getLongitude()));
                            int distance = (int) userLocation.distanceTo(cinemaLocation);
                            cinema.setDistance(distance);
                        } else {
                            Log.e(TAG, "getCinemas: user location is null");
                        }
                        allCinemas.add(cinema);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
