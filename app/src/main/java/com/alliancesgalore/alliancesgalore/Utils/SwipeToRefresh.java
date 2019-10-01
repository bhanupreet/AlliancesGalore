package com.alliancesgalore.alliancesgalore.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SwipeToRefresh extends SwipeRefreshLayout {

    private static final float REFRESH_RATE = 10f;
    private float mDownX, mDownY, scaleX, scaleY;

    public SwipeToRefresh(Context context) {
        super(context);
    }

    public SwipeToRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                return doRefresh(ev);
            case MotionEvent.ACTION_UP:
                return doRefresh(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean doRefresh(MotionEvent ev) {
        scaleX = Math.abs(ev.getX() - mDownX);
        scaleY = Math.abs(ev.getY() - mDownY);
        if (scaleY / scaleX > REFRESH_RATE) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }
}