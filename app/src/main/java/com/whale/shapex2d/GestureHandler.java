package com.whale.shapex2d;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.whale.shapex2d.geom.Vec2D;

/**
 * Created by alex on 10/02/16.
 */
public class GestureHandler extends GestureDetector.SimpleOnGestureListener {
    private DrawView mView;

    public GestureHandler(DrawView view) {
        mView = view;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mView.actionDown(new Vec2D(e.getX(), e.getY()));
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        mView.start();
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mView.setCurrentTouch(e1, e2);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
