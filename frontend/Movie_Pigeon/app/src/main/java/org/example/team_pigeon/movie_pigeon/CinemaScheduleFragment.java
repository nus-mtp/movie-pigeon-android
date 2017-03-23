package org.example.team_pigeon.movie_pigeon;

import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.example.team_pigeon.movie_pigeon.adapters.MovieListAdapter;
import org.example.team_pigeon.movie_pigeon.adapters.NowShowingListAdapter;
import org.example.team_pigeon.movie_pigeon.adapters.ScheduleListAdapter;
import org.example.team_pigeon.movie_pigeon.models.Movie;
import org.example.team_pigeon.movie_pigeon.models.Schedule;
import org.example.team_pigeon.movie_pigeon.utils.TimeUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Guo Mingxuan on 23/3/2017.
 */

public class CinemaScheduleFragment extends Fragment {
    private ArrayList<Movie> movieList = new ArrayList<>();
    private ArrayList<ArrayList<Movie>> weekList = new ArrayList<>();
    private TimeUtil timeUtil = new TimeUtil();
    private List<Date> dateList;
    private List<String> dateListString;
    private LoadingDialog loadingDialog;
    private ListView scheduleListView;
    private RadioGroup dateGroup;
    private RadioButton[] dateButtons = new RadioButton[7];
    private NowShowingListAdapter adapter;
    private String TAG = "CinemaScheduleFragment";
    private Map<Integer, Integer> dateIdMap = new HashMap<>(7);
    private RequestHttpBuilderSingleton httpRequestBuilder = RequestHttpBuilderSingleton.getInstance();
    private Gson gson = new Gson();
    private String cinemaId;
    private GlobalReceiver receiver;
    private final int weekListUpdated = 1;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        cinemaId = bundle.getString("cinemaId");
        dateList = timeUtil.getDateList();
        dateListString = timeUtil.getDateListToString_YYYYMMDD(dateList);
        new getMovieWorkingThread().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loadingDialog = new LoadingDialog(this.getActivity(),R.style.LoadingDialog);
        View view = inflater.inflate(R.layout.fragment_cinema_schedule, container, false);
        scheduleListView = (ListView) view.findViewById(R.id.cinema_movies_list);
        dateGroup = (RadioGroup) view.findViewById(R.id.rg_dates);
        for(int i = 0;i<7;i++){
            String dbId = "rb_date_" + i;
            int resID = getResources().getIdentifier(dbId, "id", "org.example.team_pigeon.movie_pigeon");
            dateButtons[i] = (RadioButton) view.findViewById(resID);
            dateButtons[i].setText(dateListString.get(i).substring(5));
            dateIdMap.put(resID,i);
        }
        receiver = new GlobalReceiver(new Handler() {
            public void handleMessage(Message msg) {
                final int what = msg.what;
                switch (what) {
                    case weekListUpdated:
                        Log.i(TAG, "Successfully get week list");
                        adapter = new NowShowingListAdapter(weekList.get(0), getActivity());
                        Log.i(TAG, "onCreateView: size of movie list " + weekList.get(0).size()
                        + weekList.get(1).size()+ " "+ weekList.get(2).size()+ " "+ weekList.get(3).size()+ " "+
                                weekList.get(4).size()+ " "+ weekList.get(5).size()+ " "+ weekList.get(6).size());
                        scheduleListView.setAdapter(adapter);
                        ((NowShowingListAdapter)scheduleListView.getAdapter()).notifyDataSetChanged();
                        dateGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                        {
                            public void onCheckedChanged(RadioGroup group, int checkedId)
                            {
                                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                                boolean isChecked = checkedRadioButton.isChecked();
                                if (isChecked)
                                {
                                    adapter = new NowShowingListAdapter(weekList.get(dateIdMap.get(checkedId)), getActivity());
                                    ((NowShowingListAdapter)scheduleListView.getAdapter()).notifyDataSetChanged();
                                }
                            }
                        });
                        break;
                }
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction("weekList");
        getActivity().registerReceiver(receiver, filter);
        return view;
    }

    private class getMovieWorkingThread extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getMovies(cinemaId);
            return null;
        }

        @Override
        protected void onPostExecute(Void V) {
            try {
                for (Movie movie : movieList) {
                    System.out.println(movie.getMovieID());
                }
                weekList = getOneWeekMovieList(movieList,dateList);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            getActivity().sendBroadcast(new Intent("weekList"));
        }
    }

    protected void getMovies(String cinemaId) {
        OkHttpClient client = httpRequestBuilder.getClient();
        Request request = httpRequestBuilder.getShowingListRequest(cinemaId);
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Log.i(TAG, "connect failed");
                throw new IOException("Unexpected code" + response);
            }
            //Convert json to Arraylist<Movie>
            movieList = gson.fromJson(response.body().charStream(), new TypeToken<ArrayList<Movie>>() {}.getType());
            if (movieList.size() == 0) {
                return;
            } else {
                return;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return;
        }
    }

    private ArrayList<ArrayList<Movie>> getOneWeekMovieList(ArrayList<Movie> movieList, List<Date> dateList) throws ParseException {
        ArrayList<ArrayList<Movie>> oneWeekMovieList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            oneWeekMovieList.add(new ArrayList<Movie>());
        }
        System.out.println(dateList.size());
        System.out.println(movieList.size());
        for (int i = 0; i < dateList.size(); i++) {
            for (int j = 0; j < movieList.size(); j++) {
                Movie movie = movieList.get(j);
                Date date = dateList.get(i);
                System.out.println("is movie null? " + movie==null);
                System.out.println("is date null? " + date==null);
                ArrayList<String> showTime = convertToShowTimeArray(date, movie.getSchedule());
                if (!showTime.isEmpty()) {
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
                Log.d(TAG, "Parsing schedule with a mistake. TimeString:" + timeString);
                return null;
            }
            calendarTwo.setTime(time);
            if (calendarOne.get(Calendar.DAY_OF_MONTH) == calendarTwo.get(Calendar.DAY_OF_MONTH)) {
                showTimeList.add(dateToString.format(time));
            }
        }
        return showTimeList;
    }

}
