package org.example.team_pigeon.movie_pigeon;



import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.FrameLayout;

public class DisplayActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    private android.app.FragmentManager fragmentManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Disable Landscape Mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_display_page);
        Bundle argument = getIntent().getBundleExtra("bundle");
        toolbar.setTitle(argument.getString("title"));
        setSupportActionBar(toolbar);
        fragmentManager = getFragmentManager();
        frameLayout = (FrameLayout) findViewById(R.id.fl_content);
        MovieListFragment movieListFragment = new MovieListFragment();
        movieListFragment.setArguments(argument);
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_content,movieListFragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

}
