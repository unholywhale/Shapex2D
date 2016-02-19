package com.whale.shapex2d.interfaces;

import android.graphics.Canvas;
import android.support.annotation.Nullable;

import com.whale.shapex2d.geom.Vec2D;

/**
 * For stationary objects
 */
public interface Stationary extends Entity {
    Vec2D getPosition();
    double getRadius();
    boolean isDelete();
    void cancel();
    void action(Vec2D position);
    Moving shoot();
    void hit();
    void die();
    void draw(Canvas canvas, @Nullable Vec2D currentTouch);
    void draw(Canvas canvas);
}
