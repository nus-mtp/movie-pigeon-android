package org.example.team_pigeon.movie_pigeon;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.example.team_pigeon.movie_pigeon.adapters.CinemaAdapter;
import org.example.team_pigeon.movie_pigeon.adapters.NowShowingListAdapter;
import org.example.team_pigeon.movie_pigeon.eventCenter.AddMovieToMovieListEvent;
import org.example.team_pigeon.movie_pigeon.eventCenter.DeleteMovieFromMovieListEvent;
import org.example.team_pigeon.movie_pigeon.eventCenter.UpdateMovieListEvent;
import org.example.team_pigeon.movie_pigeon.models.Cinema;
import org.example.team_pigeon.movie_pigeon.models.Movie;
import org.example.team_pigeon.movie_pigeon.models.Schedule;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NowShowingFragment extends Fragment implements AdapterView.OnItemSelectedListener,AdapterView.OnItemClickListener{
    private static final String TAG = "NowShowingFragment";
    private static final String PROVIDER_GV = "gv";
    private static final String PROVIDER_SB = "sb";
    private static final String PROVIDER_CATHAY = "cathay";
    private static final String GET_CINEMAS = "1";
    private static final String GET_MOVIES = "2";
    private static final int POS_GV = 1;
    private static final int POS_SB = 2;
    private static final int POS_CATHAY = 3;
    private Spinner brandSpinner, outletSpinner, dateSpinner;
    private ListView movieListView;
    private Gson gson = new Gson();
    private ArrayAdapter<String> brandAdapter;
    private String[] brands;
    private RequestHttpBuilderSingleton httpRequestBuilder = RequestHttpBuilderSingleton.getInstance();
    private ArrayList<Cinema> cinemas = new ArrayList<>();
    private ArrayList<Cinema> gvCinemas = new ArrayList<>();
    private ArrayList<Cinema> sbCinemas = new ArrayList<>();
    private ArrayList<Cinema> cathayCinemas = new ArrayList<>();
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


    public NowShowingFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now_showing, container, false);
        bindViews(view);
        dateList = getDateList();
        dateListInString = getDateListToString(dateList);
        brands = this.getActivity().getResources().getStringArray(R.array.cinemaBrands);
        brandAdapter = new ArrayAdapter<>(this.getActivity(), R.layout.spinner_list_item,brands);
        brandSpinner.setAdapter(brandAdapter);
        nowShowingTask = new NowShowingTask();
        nowShowingTask.execute(GET_CINEMAS);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        view.requestFocus();
        return view;
    }

    private void bindViews(View view) {
        brandSpinner = (Spinner) view.findViewById(R.id.spinner_cinema_brand);
        outletSpinner = (Spinner) view.findViewById(R.id.spinner_cinema_outlet);
        dateSpinner = (Spinner) view.findViewById(R.id.spinner_date);
        movieListView = (ListView) view.findViewById(R.id.list_now_showing);
        brandSpinner.setOnItemSelectedListener(this);
        outletSpinner.setOnItemSelectedListener(this);
        dateSpinner.setOnItemSelectedListener(this);
        movieListView.setOnItemClickListener(this);
    }

    private ArrayList<ArrayList<Movie>> getOneWeekMovieList (ArrayList<Movie> movieList, List<Date> dateList) throws ParseException {
        ArrayList<ArrayList<Movie>> oneWeekMovieList = new ArrayList<>();
        for(int i=0;i<7;i++){
            oneWeekMovieList.add(new ArrayList<Movie>());
        }
        for(int i = 0; i<dateList.size();i++){
            for(int j = 0; j<movieList.size();j++){
                Movie movie = movieList.get(j);
                Date date = dateList.get(i);
                ArrayList<String> showTime = convertToShowTimeArray(date,movie.getSchedule());
                if(!showTime.isEmpty()){
                    movie.setShowTimes(showTime);
                    oneWeekMovieList.get(i).add(movie);
                }
            }
        }
        return oneWeekMovieList;
    }

    private ArrayList<String> convertToShowTimeArray(Date date, ArrayList<Schedule> scheduleArrayList) {
        String timeString;
        Calendar calendarOne = Calendar.getInstance();
        Calendar calendarTwo = Calendar.getInstance();
        calendarOne.setTime(date);
        ArrayList<String> showTimeList = new ArrayList<>();
        Date time;
        SimpleDateFormat stringToDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat dateToString = new SimpleDateFormat("HH:mm");
        for (Schedule schedule : scheduleArrayList) {
            timeString = schedule.getTime();
            try {
                time = stringToDate.parse(timeString);
            } catch (ParseException e) {
                Log.d(TAG,"Parsing schedule with a mistake. TimeString:"+timeString);
                return null;
            }
            calendarTwo.setTime(time);
            if (calendarOne.get(Calendar.DAY_OF_MONTH) == calendarTwo.get(Calendar.DAY_OF_MONTH)) {
                showTimeList.add(dateToString.format(time));
            }
        }
        return showTimeList;
    }

    private List<Date> getDateList(){
        List<Date> week = new ArrayList<>();
        Calendar timeToAdd = Calendar.getInstance();
        Date date;
        for(int i=0;i<7;i++){
            date = timeToAdd.getTime();
            week.add(date);
            timeToAdd.add(Calendar.DATE,1);
        }
        return week;
    }

    private List<String> getDateListToString(List<Date> week){
        List<String> stringList = new ArrayList<>();
        stringList.add("Please Choose Date");
        for(Date date:week){
            stringList.add(new SimpleDateFormat("MM-dd E", Locale.ENGLISH).format(date));
        }
        return stringList;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String cinemaId;
        if(isCinemasLoaded) {
            switch (parent.getId()) {
                case R.id.spinner_cinema_brand:
                    cinemaAdapter = getOutletSpinnerAdapter(position);
                    if(cinemaAdapter!=null) {
                        outletSpinner.setAdapter(cinemaAdapter);
                        outletSpinner.setVisibility(View.VISIBLE);
                        dateSpinner.setVisibility(View.INVISIBLE);
                    }
                    else{
                        outletSpinner.setVisibility(View.GONE);
                    }
                    dateSpinner.setVisibility(View.GONE);
                    movieListView.setVisibility(View.GONE);
                    break;
                case R.id.spinner_cinema_outlet:
                    if(position!=0) {
                        cinemaId = cinemaAdapter.getItem(position).getId();
                        nowShowingTask = new NowShowingTask();
                        nowShowingTask.execute(GET_MOVIES, cinemaId);
                    }
                    else {
                        dateSpinner.setVisibility(View.GONE);
                    }
                    movieListView.setVisibility(View.GONE);
                    break;
                case R.id.spinner_date:
                    if(position!=0) {
                        currentDay = position-1;
                        if(nowShowingListAdapter==null) {
                            //init adapter
                            moviesOfTheDay.addAll(oneWeekMovieList.get(currentDay));
                            nowShowingListAdapter = new NowShowingListAdapter(moviesOfTheDay, this.getContext());
                            movieListView.setAdapter(nowShowingListAdapter);
                        }
                        else{
                            moviesOfTheDay.clear();
                            moviesOfTheDay.addAll(oneWeekMovieList.get(currentDay));
                            nowShowingListAdapter.notifyDataSetChanged();
                        }
                        movieListView.setVisibility(View.VISIBLE);
                    }
                    else {
                        movieListView.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent displayActivityIntent = new Intent(getActivity(), DisplayActivity.class);
        Bundle arguments = new Bundle();
        arguments.putSerializable("movie",nowShowingListAdapter.getItem(position));
        arguments.putString("type", "moviePage");
        arguments.putInt("position",position);
        arguments.putString("title", nowShowingListAdapter.getItem(position).getTitle());
        displayActivityIntent.putExtra("bundle", arguments);
        getActivity().startActivity(displayActivityIntent);
    }

    private CinemaAdapter getOutletSpinnerAdapter(int position) {
        switch (position) {
            case POS_GV:
                return new CinemaAdapter(this.getContext(), gvCinemas);
            case POS_CATHAY:
                return new CinemaAdapter(this.getContext(), cathayCinemas);
            case POS_SB:
                return new CinemaAdapter(this.getContext(), sbCinemas);
        }
        return null;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Subscribe
    public void onEvent(AddMovieToMovieListEvent event){
        nowShowingListAdapter.addMovieItemToAdapter(event.movie, event.position);
        Log.i(TAG, "New movie is added to local list");
    }

    @Subscribe
    public void onEvent(DeleteMovieFromMovieListEvent event){
        nowShowingListAdapter.removeMovieItemToAdapter(event.position);
        Log.i(TAG, "A movie is removed from local list");
    }

    @Subscribe
    public void onEvent(UpdateMovieListEvent event){
        nowShowingListAdapter.updateMovieItemToAdapter(event.movie, event.position);
        Log.i(TAG, "A movie is updated to local list at pos "+String.valueOf(event.position));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private class NowShowingTask extends AsyncTask<String, Integer, Void> {
        private final int SUCCESSFUL_CINEMALIST = 0;
        private final int SUCCESSFUL_MOVIELIST = 1;
        private final int ERROR = 2;
        private final int NO_RESULT = 3;
        private final int NO_INTERNET = 4;
        private String requestType, cinemaId;
        int status;

        @Override
        protected Void doInBackground(String... params) {
            requestType = params[0];
            if (requestType.equals(GET_CINEMAS)) {
                getCinemas();
            } else if (requestType.equals(GET_MOVIES)) {
                cinemaId = params[1];
                getMovies(cinemaId);
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
                    break;
                case SUCCESSFUL_MOVIELIST:
                    Log.i(TAG,"Request is completed");
                    try {
                        oneWeekMovieList = getOneWeekMovieList(movieList,dateList);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    dateSpinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_list_item, dateListInString));
                    dateSpinner.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    Toast.makeText(getContext(), "Connection error, please check your connection", Toast.LENGTH_SHORT).show();
                    break;

                case NO_RESULT:
                    Toast.makeText(getContext(), "Sorry, there is no results", Toast.LENGTH_SHORT).show();
                    break;

                case NO_INTERNET:
                    Toast.makeText(getContext(), "Connection error, please make sure that you have Internet connection.", Toast.LENGTH_SHORT).show();
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
                        switch (cinema.getProvider()) {
                            case PROVIDER_CATHAY:
                                cathayCinemas.add(cinema);
                                break;
                            case PROVIDER_GV:
                                gvCinemas.add(cinema);
                                break;
                            case PROVIDER_SB:
                                sbCinemas.add(cinema);
                                break;
                        }
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        protected void getMovies(String cinemaId) {
            OkHttpClient client = httpRequestBuilder.getClient();
            Request request = httpRequestBuilder.getShowingListRequest(cinemaId);
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Log.i(TAG, "connect failed");
                    status = ERROR;
                    throw new IOException("Unexpected code" + response);
                }
                //Convert json to Arraylist<Movie>
                movieList = gson.fromJson(response.body().charStream(), new TypeToken<ArrayList<Movie>>() {}.getType());
                if (movieList.size() == 0) {
                    status = NO_RESULT;
                    return;
                } else {
                    status = SUCCESSFUL_MOVIELIST;
                    return;
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return;
            }
        }
    }
}
