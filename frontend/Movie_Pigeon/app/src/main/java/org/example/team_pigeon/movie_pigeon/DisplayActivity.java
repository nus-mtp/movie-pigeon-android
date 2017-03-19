package org.example.team_pigeon.movie_pigeon;



import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

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
        if(!ImageLoader.getInstance().isInited()){
            initImageLoaderConfig();
        }
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
        else if(type.equals("search")){
            SearchPageFragment searchPageFragment = new SearchPageFragment();
            searchPageFragment.setArguments(argument);
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_content, searchPageFragment);
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

    private void initImageLoaderConfig(){
        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "MoviePigeon/cache/poster");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(5)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(5 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(200)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }

}
