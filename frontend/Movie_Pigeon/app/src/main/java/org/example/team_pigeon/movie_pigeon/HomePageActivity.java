package org.example.team_pigeon.movie_pigeon;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class HomePageActivity extends AppCompatActivity {
    private TextView txt_title;
    private FrameLayout fl_content;
    private Context mContext;
    private android.app.FragmentManager fragmentManager = null;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initImageLoaderConfig();
        setContentView(R.layout.activity_home_page);
        mContext = HomePageActivity.this;
        fragmentManager = getFragmentManager();
        bindViews();
        SearchPageFragment searchPageFragment = new SearchPageFragment();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_content,searchPageFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed(){
        if(fragmentManager.getBackStackEntryCount() == 0){
            if((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "Tap again to exit", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        } else {
            fragmentManager.popBackStack();
        }
    }

    private void bindViews(){
        fl_content = (FrameLayout) findViewById(R.id.fl_content);
    }

    private void initImageLoaderConfig(){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(5)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }
}
