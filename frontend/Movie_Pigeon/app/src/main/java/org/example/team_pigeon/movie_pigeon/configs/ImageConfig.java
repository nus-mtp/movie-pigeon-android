package org.example.team_pigeon.movie_pigeon.configs;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.example.team_pigeon.movie_pigeon.R;

/**
 * Created by SHENGX on 2017/3/3.
 */

public class ImageConfig {
    static DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.mipmap.image_no_poster_found)
            .showImageOnLoading(R.mipmap.image_poster_loading)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();
    public DisplayImageOptions getDisplayImageOption(){
        return options;
    }
}
