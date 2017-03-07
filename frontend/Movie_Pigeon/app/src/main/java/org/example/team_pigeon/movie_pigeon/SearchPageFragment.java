package org.example.team_pigeon.movie_pigeon;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.google.gson.Gson;

import org.example.team_pigeon.movie_pigeon.models.Movie;

import java.util.ArrayList;

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
        return view;
    }

}
