package org.example.team_pigeon.movie_pigeon.costomizedViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by SHENGX on 2017/3/12.
 */

public class CenterTextView extends TextView
{
    private StaticLayout myStaticLayout;
    private TextPaint textPaint;

    public CenterTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        initView();
    }

    private void initView()
    {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(getTextSize());
        textPaint.setColor(getCurrentTextColor());
        myStaticLayout = new StaticLayout(getText(), textPaint, getWidth(), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        myStaticLayout.draw(canvas);
    }
}