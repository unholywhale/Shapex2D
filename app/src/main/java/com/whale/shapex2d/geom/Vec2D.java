package com.whale.shapex2d.geom;

import android.graphics.Point;

public class Vec2D {
    public double x, y;

    public Vec2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2D() {
    }

    public static int distance(Vec2D v1, Vec2D v2) {
        double x = Math.abs(v1.x - v2.x);
        double y = Math.abs(v1.y - v2.y);
        double result = Math.sqrt(x*x + y*y);
        return (int) result;
    }

    public Vec2D subtract(Vec2D v) {
        return new Vec2D(this.x - v.x, this.y - v.y);
    }

    public void normalize(int i) {
        float length = (float) Math.sqrt(x*x + y*y);
        float invLength = i / length;
        x *= invLength;
        y *= invLength;
    }

}