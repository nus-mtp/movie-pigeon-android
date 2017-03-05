package org.example.team_pigeon.movie_pigeon;



import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class DisplayActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    private android.app.FragmentManager fragmentManager = null;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Disable Landscape Mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_display_page);
        Bundle argument = getIntent().getBundleExtra("bundle");
        type = argument.getString("type");
        toolbar.setTitle(argument.getString("title"));
        setSupportActionBar(toolbar);
        fragmentManager = getFragmentManager();
        frameLayout = (FrameLayout) findViewById(R.id.fl_content);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //Handle single movie info passed in from showing page
        if(type.equals("moviePage")){
            MoviePageFragment moviePageFragment = new MoviePageFragment();
            moviePageFragment.setArguments(argument);
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_content,moviePageFragment);
            fragmentTransaction.commit();
        }
        //Handle movie list requests
        else {
            MovieListFragment movieListFragment = new MovieListFragment();
            movieListFragment.setArguments(argument);
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_content, movieListFragment);
            fragmentTransaction.commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(fragmentManager.getBackStackEntryCount()==0){
                    super.onBackPressed();
                }
                else{
                    fragmentManager.popBackStack();
                }
        }
        return false;
    }

}
