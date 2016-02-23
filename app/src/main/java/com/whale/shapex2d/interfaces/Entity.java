package com.whale.shapex2d.interfaces;

import android.graphics.Canvas;

import com.whale.shapex2d.geom.Vec2D;

import java.util.ArrayList;

/**
 * Created by alex on 17/02/16.
 */
public interface Entity {
    Vec2D getPosition();
    Vec2D getNextPos();
    Vec2D getVelocity();
    void setVelocity(Vec2D velocity);
    double getMass();
    double getRadius();
    void setRadius(double r);
    boolean isStationary();
    int move();
    void hit();
    void hit(double dmg);
    void die();
    boolean isDead();
    boolean isDelete();
    void draw(Canvas canvas);
    ArrayList<Entity> shoot();
}
