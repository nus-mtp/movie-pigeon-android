package org.example.team_pigeon.movie_pigeon;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.example.team_pigeon.movie_pigeon.adapters.ScheduleListAdapter;
import org.example.team_pigeon.movie_pigeon.configs.ImageConfig;
import org.example.team_pigeon.movie_pigeon.models.Movie;
import org.example.team_pigeon.movie_pigeon.models.Schedule;
import org.example.team_pigeon.movie_pigeon.utils.TimeUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MovieScheduleFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static String TAG = "MovieScheduleFragment";
    private ListView scheduleListView;
    private ImageView poster;
    private TextView lengthText, genreText;
    private Spinner dateSpinner;
    private TimeUtil timeUtil = new TimeUtil();
    private List<Date> dateList;
    private List<String> dateListString;
    private ArrayList<Schedule> rawSchedules;
    private ArrayList<ArrayList<Schedule>> schedulesOfAWeek = new ArrayList<>();
    private ArrayList<ScheduleListAdapter> adapterList = new ArrayList<>();
    private Movie movie;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = new ImageConfig().getDisplayImageOption();
    private Boolean isDataReady = false;
    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        movie = (Movie)bundle.getSerializable("movie");
        dateList = timeUtil.getDateList();
        dateListString = timeUtil.getDateListToString_YYYYMMDD(dateList);
        for (int i = 0; i < 7; i++) {
            schedulesOfAWeek.add(new ArrayList<Schedule>());
            adapterList.add(new ScheduleListAdapter(schedulesOfAWeek.get(i), getActivity()));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loadingDialog = new LoadingDialog(this.getActivity(),R.style.LoadingDialog);
        View view = inflater.inflate(R.layout.fragment_movie_schedule, container, false);
        bindView(view);

        dateSpinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_list_item, dateListString));
        dateSpinner.setOnItemSelectedListener(this);
        //Set movie info views
        setMovieInfoViews();

        //Process raw schedules
        GetScheduleTask task = new GetScheduleTask();
        task.execute(movie.getMovieID());

        return view;
    }

    //This method scan through the raw schedule data one by one, and add list item views where is needed.
    //FYI, the raw date is sorted by date->cinema->movieType->time
    private void prepareScheduleObjects() {
        Boolean isValidDate = false;
        int currentId = -1;
        int currentDay = 0;
        String currentDate = "";
        String currentType = "";
        Schedule currentSchedule = null;
        ArrayList<String> currentShowtimeArray = null;

        //Firstly, if the starting date of schedule is later than today, find it out.
        int startingDay = -1;
        for(int i=0;i<dateListString.size();i++){
            if(rawSchedules.get(0).getDate().equals(dateListString.get(i))){
                startingDay = i;
                break;
            }
        }
        if(startingDay>=0){
            isValidDate = true;
            currentSchedule = rawSchedules.get(0);
            currentId = currentSchedule.getCinemaId();
            currentDate = currentSchedule.getDate();
            currentType = currentSchedule.getType();
            currentDay = startingDay;
            currentSchedule.setShowTimeArray(new ArrayList<String>());
            currentShowtimeArray = currentSchedule.getShowTimeArray();
            ScheduleListAdapter currentAdapter = adapterList.get(currentDay);
            currentAdapter.addCinemaToAdapter(currentSchedule);
        }
        //Secondly, look at each schedule data, the logic is:
        //1. find the starting position of with today's date, if the starting position is earlier than today.
        //2. Compare each schedule with current date, cinemaId and type accordingly, if all are the same, then add its showtime into current schedule's showtime array.
        //3. If any data is different, save the previous showtime array, and load this new schedule as current schedule.
        //4. If the difference is date or cinemaId, meaning we need a new cinema title, add it to the adapter.
        //5. If the date is different, increment currentDay, new data will be saved in a new adapter. Each day has a separated adapter to make the listView able to update.
        for (int i = 0; i < rawSchedules.size() && currentDay < 7; ) {
            if (isValidDate) {
                if (rawSchedules.get(i).getDate().equals(currentDate)) {
                    if (rawSchedules.get(i).getCinemaId() == currentId) {
                        if (rawSchedules.get(i).getType().equals(currentType)) {
                            currentShowtimeArray.add(rawSchedules.get(i).getShowtime().substring(0, 5));
                            i++;
                            //peek the next schedule, avoid index out of boundary
                            if (i == rawSchedules.size() || !rawSchedules.get(i).getType().equals(currentType) || rawSchedules.get(i).getCinemaId()!=currentId || !rawSchedules.get(i).getDate().equals(currentDate)) {
                                adapterList.get(currentDay).addScheduleItemToAdapter(currentSchedule);
                            }
                        } else {
                            currentSchedule = rawSchedules.get(i);
                            currentType = rawSchedules.get(i).getType();
                            currentSchedule.setShowTimeArray(new ArrayList<String>());
                            currentShowtimeArray = currentSchedule.getShowTimeArray();
                        }
                    } else {
                        currentSchedule = rawSchedules.get(i);
                        adapterList.get(currentDay).addCinemaToAdapter(currentSchedule);
                        currentId = currentSchedule.getCinemaId();
                        currentType = currentSchedule.getType();
                        currentSchedule.setShowTimeArray(new ArrayList<String>());
                        currentShowtimeArray = currentSchedule.getShowTimeArray();
                    }
                } else {
                    currentSchedule = rawSchedules.get(i);
                    currentDate = currentSchedule.getDate();
                    currentDay++;
                    adapterList.get(currentDay).addCinemaToAdapter(currentSchedule);
                    currentId = currentSchedule.getCinemaId();
                    currentType = currentSchedule.getType();
                    currentSchedule.setShowTimeArray(new ArrayList<String>());
                    currentShowtimeArray = currentSchedule.getShowTimeArray();
                }

            }
            //Find out the starting position, with today's date, ignore the outdated data
            else {
                if (rawSchedules.get(i).getDate().equals(dateListString.get(0))) {
                    currentSchedule = rawSchedules.get(i);
                    currentId = currentSchedule.getCinemaId();
                    currentDate = currentSchedule.getDate();
                    currentType = currentSchedule.getType();
                    currentSchedule.setShowTimeArray(new ArrayList<String>());
                    currentShowtimeArray = currentSchedule.getShowTimeArray();
                    ScheduleListAdapter currentAdapter = adapterList.get(currentDay);
                    currentAdapter.addCinemaToAdapter(currentSchedule);
                    isValidDate = true;
                } else {
                    i++;
                }
            }
        }
        isDataReady = true;
    }

    private void setMovieInfoViews() {
        imageLoader.displayImage(movie.getPosterURL(), poster, options);
        if (movie.getLength() != null) {
            lengthText.setText(movie.getLength() + " Min");
        }
        if (movie.getGenres() != null) {
            genreText.setText(movie.getGenres());
        }
    }

    private void bindView(View view) {
        scheduleListView = (ListView) view.findViewById(R.id.list_schedules);
        poster = (ImageView) view.findViewById(R.id.image_schedule_poster);
        lengthText = (TextView) view.findViewById(R.id.text_schedule_length);
        genreText = (TextView) view.findViewById(R.id.text_schedule_genre);
        dateSpinner = (Spinner) view.findViewById(R.id.spinner_date_schedule);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner_date_schedule && isDataReady) {
            scheduleListView.setAdapter(adapterList.get(position));
            adapterList.get(position).notifyDataSetChanged();
            if(adapterList.get(position).getCount()==0){
                Toast.makeText(getActivity(), "Sorry, there is no schedule available on this day.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Async thread to handle search request
    private class GetScheduleTask extends AsyncTask<String, Integer, Integer> {
        private final int SUCCESSFUL_SCHEDULE = 0;
        private final int NO_INTERNET = 1;
        private int status;
        RequestHttpBuilderSingleton requestHttpBuilder = RequestHttpBuilderSingleton.getInstance();

        @Override
        protected Integer doInBackground(String... params) {
            String movieId = params[0];

            Request request = requestHttpBuilder.getScheduleOfMovie(movieId);
            try {
                OkHttpClient client = requestHttpBuilder.getClient();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code" + response);
                } else {
                    status = SUCCESSFUL_SCHEDULE;
                    Gson gson = new Gson();
                    rawSchedules = gson.fromJson(response.body().charStream(), new TypeToken<ArrayList<Schedule>>() {}.getType());
                    }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return NO_INTERNET;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "Get schedule request is initialised");
            loadingDialog.show();
            return;
        }

        @Override
        protected void onPostExecute(Integer params) {
            switch (status) {
                case SUCCESSFUL_SCHEDULE:
                    Log.i(TAG, "Get Schedule succefully");
                    prepareScheduleObjects();
                    scheduleListView.setAdapter(adapterList.get(0));
                    loadingDialog.dismiss();
                    break;

                case NO_INTERNET:
                    Toast.makeText(getActivity(), "Connection error, please make sure that you have Internet connection.", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }
}
