package com.example.embr6monitoringapp.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;


public class MarqueeScrollView extends HorizontalScrollView {

    public MarqueeScrollView(Context context) {
        super(context);
    }

    public MarqueeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}