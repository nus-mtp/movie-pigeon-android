package org.example.team_pigeon.movie_pigeon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.example.team_pigeon.movie_pigeon.adapters.HomeViewPagerAdapter;

public class HomePageActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener{
    private TextView txt_title;
    private ViewPager viewPager;
    private RadioButton rb_recommendation;
    private RadioButton rb_me;
    private RadioGroup rg_tab_bar;
    private FrameLayout fl_content;
    private Context mContext;
    private android.app.FragmentManager fragmentManager = null;
    private HomeViewPagerAdapter homeViewPagerAdapter;
    private RequestHttpBuilderSingleton requestHttpBuilderSingleton = RequestHttpBuilderSingleton.getInstance();
    private long exitTime = 0;

    public static final int PAGE_RECOMMENDATION = 0;
    public static final int PAGE_ME = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initImageLoaderConfig();
        requestHttpBuilderSingleton.setToken(getIntent().getExtras().getString("Token").trim());
        setContentView(R.layout.activity_home_page);
        homeViewPagerAdapter = new HomeViewPagerAdapter(getSupportFragmentManager());
        fragmentManager = getFragmentManager();
        bindViews();
        rb_recommendation.setChecked(true);
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
        rg_tab_bar = (RadioGroup)findViewById(R.id.rg_tab_bar);
        rb_recommendation = (RadioButton)findViewById(R.id.rb_home);
        rb_me = (RadioButton)findViewById(R.id.rb_me);
        rg_tab_bar.setOnCheckedChangeListener(this);
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        viewPager.setAdapter(homeViewPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(this);
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(state == 2) {
            switch (viewPager.getCurrentItem()) {
                case PAGE_RECOMMENDATION:
                    rb_recommendation.setChecked(true);
                    break;
                case PAGE_ME:
                    rb_me.setChecked(true);
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId){
            case R.id.rb_home:
                viewPager.setCurrentItem(PAGE_RECOMMENDATION);
                break;
            case R.id.rb_me:
                viewPager.setCurrentItem(PAGE_ME);
                break;
        }
    }
}
