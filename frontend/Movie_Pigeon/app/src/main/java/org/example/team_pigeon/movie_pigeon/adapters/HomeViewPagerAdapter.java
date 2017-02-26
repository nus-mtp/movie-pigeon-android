package org.example.team_pigeon.movie_pigeon.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.example.team_pigeon.movie_pigeon.HomePageActivity;
import org.example.team_pigeon.movie_pigeon.MeFragment;
import org.example.team_pigeon.movie_pigeon.RecommendationFragment;

/**
 * Created by SHENGX on 2017/2/15.
 */

public class HomeViewPagerAdapter extends FragmentPagerAdapter {
    private final int PAGER_COUNT = 2;
    private RecommendationFragment recommendationFragment = null;
    private MeFragment meFragment = null;

    public HomeViewPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
        recommendationFragment = new RecommendationFragment();
        meFragment = new MeFragment();
    }
    @Override
    public Fragment getItem(int position) {
       Fragment fragment = null;
        switch (position){
            case HomePageActivity.PAGE_RECOMMENDATION:
                fragment = recommendationFragment;
                break;
            case HomePageActivity.PAGE_ME:
                fragment = meFragment;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }
}
