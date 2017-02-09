package org.example.team_pigeon.movie_pigeon;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import org.example.team_pigeon.movie_pigeon.models.Movie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Handler;

import static android.content.ContentValues.TAG;

/**
 * Created by SHENGX on 2017/2/5.
 */

public class SearchPageFragment extends Fragment {
    private Button btnSearch;
    private RadioGroup rgrpSearchBy;
    private RadioButton rbtnTitle,rbtnActor,rbtnCountry;
    private EditText etSearch;
    private android.app.FragmentManager fragmentManager;
    private SearchRequestHttpBuilder searchRequestHttpBuilder;
    private ArrayList<Movie> movies;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_page, container, false);
        fragmentManager = getFragmentManager();
        btnSearch = (Button) view.findViewById(R.id.button_search);
        rgrpSearchBy = (RadioGroup)view.findViewById(R.id.rg_search_by);
        etSearch = (EditText)view.findViewById(R.id.editText_search);

        rgrpSearchBy.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
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
                android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                MovieListFragment movieListFragment = new MovieListFragment();
                Bundle arguments = new Bundle();
                searchRequestHttpBuilder = new SearchRequestHttpBuilder(etSearch.getText().toString());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            searchRequestHttpBuilder.run();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (searchRequestHttpBuilder.getMovies()!=null){

                        }
                    }
                }).start();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                movies = searchRequestHttpBuilder.getMovies();
                arguments.putSerializable("movies",movies);
                movieListFragment.setArguments(arguments);
                fragmentTransaction.replace(R.id.fl_content,movieListFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    //To create a list of movies for testing purpose
    public ArrayList<Movie> testCase(int n){
        ArrayList<Movie> movies = new ArrayList<>();
        for(int i=0;i<n;i++){
            Movie movie = new Movie("movie"+i,i+"");
            movie.setPosterURL("https://s-media-cache-ak0.pinimg.com/736x/e8/5f/e6/e85fe6cf9815c33aa93f1656623b4442.jpg");
            movie.setGenres("Action, Comedy, Short");
            movie.setPlot("In the deadliest incident, 53 people died in one village after an avalanche in Nuristan, a north-eastern Afghan province on the Pakistan border. Thirteen people were also killed in an avalanche in northern Pakistan, nine of them in the town of Chitral. Dozens of houses have been destroyed and people were reported to have frozen to death, trapped in cars. Afghanistan's minister of state natural disasters, Wais Ahmad Barmak, confirmed the number of dead to BBC Afghan. There were also avalanches to the north of the Afghan capital, Kabul. \"Avalanches have buried two entire villages,\" a spokesman for the ministry told news agency AFP of the Barg Matal area in Nuristan. The neighbouring mountainous province of Badakhshan was also badly hit by snow storms.");
            movies.add(movie);
        }
        return movies;
    }
}
