package org.example.team_pigeon.movie_pigeon.costomizedViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by SHENGX on 2017/3/19.
 */

public class MultipleColGridView extends GridView {
    public MultipleColGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultipleColGridView(Context context) {
        super(context);
    }

    public MultipleColGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}

