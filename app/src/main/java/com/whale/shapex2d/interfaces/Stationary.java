package com.whale.shapex2d.interfaces;

import android.graphics.Canvas;

/**
 * For stationary objects
 */
public interface Stationary {
    boolean intersects(int x, int y);
    void disappear();
    void draw(Canvas canvas);
}
