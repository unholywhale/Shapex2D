package com.whale.shapex2d.interfaces;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.whale.shapex2d.geom.Vec2D;

/**
 * For stationary objects
 */
public interface Stationary {
    void hit();
    boolean isDelete();
    void die();
    void cancel();
    Vec2D getPosition();
    double getRadius();
    void draw(Canvas canvas, Vec2D currentTouch);
    void draw(Canvas canvas);
}
