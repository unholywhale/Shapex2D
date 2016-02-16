package com.whale.shapex2d.interfaces;

import android.graphics.Canvas;
import com.whale.shapex2d.enums.Boundaries;
import com.whale.shapex2d.geom.Vec2D;

/**
 * For movable objects on the screen
 */
public interface Moving {
    Vec2D getNextPos();
    Vec2D getPosition();
    Vec2D getVelocity();
    double getMass();
    void setVelocity(Vec2D velocity);
    void setRadius(double r);
    double getRadius();
    int move();
    void hit();
    void hit(double dmg);
    void die();
    boolean isDead();
    boolean isDelete();
    void draw(Canvas canvas);
}