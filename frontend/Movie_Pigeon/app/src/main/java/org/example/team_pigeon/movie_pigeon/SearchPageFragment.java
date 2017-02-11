package org.example.team_pigeon.movie_pigeon;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.example.team_pigeon.movie_pigeon.models.Movie;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by SHENGX on 2017/2/5.
 */

public class SearchPageFragment extends Fragment {
    private Button btnSearch;
    private RadioGroup rgrpSearchBy;
    private EditText etSearch;
    private FragmentManager fragmentManager;
    private ArrayList<Movie> movies;
    private Gson gson = new Gson();
    private SearchTask searchTask;
    private Bundle arguments;
    private MovieListFragment movieListFragment = new MovieListFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_page, container, false);
        fragmentManager = getFragmentManager();
        btnSearch = (Button) view.findViewById(R.id.button_search);
        rgrpSearchBy = (RadioGroup) view.findViewById(R.id.rg_search_by);
        //To be Implemented in next version
        rgrpSearchBy.setVisibility(View.GONE);
        etSearch = (EditText) view.findViewById(R.id.editText_search);
        rgrpSearchBy.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.button_title:
                        etSearch.setHint("Search movies by title...");
                        break;
                    case R.id.button_actor:
                        etSearch.setHint("Search movies by actor...");
                        break;
                    case R.id.button_country:
                        etSearch.setHint("Search movies by country...");
                        break;
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTask = new SearchTask();
                searchTask.execute(etSearch.getText().toString());
            }
        });
        return view;
    }

    private class SearchTask extends AsyncTask<String, Integer, Void> {
        private final int SUCCESSFUL = 0;
        private final int ERROR = 1;
        private final int NO_RESULT = 2;
        int status = SUCCESSFUL;

        @Override
        protected Void doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new SearchRequestHttpBuilder(params[0]).getRequest();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    status = ERROR;
                    throw new IOException("Unexpected code" + response);
                }
                //Convert json to Arraylist<Movie>
                movies = gson.fromJson(response.body().charStream(), new TypeToken<ArrayList<Movie>>() {
                }.getType());
                if (movies.size() == 0) {
                    status = NO_RESULT;
                }
                return null;
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;

        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "New search request is initialised");
            btnSearch.setEnabled(false);
            return;
        }

        @Override
        protected void onPostExecute(Void params) {
            switch (status) {
                case SUCCESSFUL:
                    Log.i(TAG, "Search is completed");
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    arguments = new Bundle();
                    arguments.putSerializable("movies", movies);
                    movieListFragment.setArguments(arguments);
                    fragmentTransaction.replace(R.id.fl_content, movieListFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;

                case ERROR:
                    Toast.makeText(getContext(), "Connection error, please check your connection", Toast.LENGTH_SHORT).show();
                    btnSearch.setEnabled(true);
                    break;

                case NO_RESULT:
                    Toast.makeText(getContext(), "Sorry, the search has no results", Toast.LENGTH_SHORT).show();
                    btnSearch.setEnabled(true);
                    break;
            }

        }
    }

    //To create a list of movies for testing purpose
//    public ArrayList<Movie> testCase(int n){
//        ArrayList<Movie> movies = new ArrayList<>();
//        for(int i=0;i<n;i++){
//            Movie movie = new Movie("movie"+i,i+"");
//            movie.setPosterURL("https://s-media-cache-ak0.pinimg.com/736x/e8/5f/e6/e85fe6cf9815c33aa93f1656623b4442.jpg");
//            movie.setGenres("Action, Comedy, Short");
//            movie.setPlot("In the deadliest incident, 53 people died in one village after an avalanche in Nuristan, a north-eastern Afghan province on the Pakistan border. Thirteen people were also killed in an avalanche in northern Pakistan, nine of them in the town of Chitral. Dozens of houses have been destroyed and people were reported to have frozen to death, trapped in cars. Afghanistan's minister of state natural disasters, Wais Ahmad Barmak, confirmed the number of dead to BBC Afghan. There were also avalanches to the north of the Afghan capital, Kabul. \"Avalanches have buried two entire villages,\" a spokesman for the ministry told news agency AFP of the Barg Matal area in Nuristan. The neighbouring mountainous province of Badakhshan was also badly hit by snow storms.");
//            movies.add(movie);
//        }
//        return movies;
//    }


}
