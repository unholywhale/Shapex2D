package com.whale.shapex2d.interfaces;

import android.graphics.Canvas;
import com.whale.shapex2d.enums.Boundaries;
import com.whale.shapex2d.geom.Vec2D;

/**
 * For movable objects on the screen
 */
public interface Movable {
    Vec2D getNextPos();
    Vec2D getPosition();
    Vec2D getVelocity();
    void setVelocity(Vec2D velocity);
    void setRadius(int r);
    int getRadius();
    int move();
    int move(int velX, int velY);
    boolean intersects(int x, int y);
    Boundaries getBoundary(int xBorder, int yBorder);
    void disappear();
    void draw(Canvas canvas);
}
