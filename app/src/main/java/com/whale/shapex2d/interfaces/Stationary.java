package com.whale.shapex2d.interfaces;

import android.graphics.Canvas;

import com.whale.shapex2d.geom.Vec2D;

/**
 * For stationary objects
 */
public interface Stationary {
    boolean isGrowing();
    void die();
    Vec2D getPosition();
    double getRadius();
    void draw(Canvas canvas);
}
