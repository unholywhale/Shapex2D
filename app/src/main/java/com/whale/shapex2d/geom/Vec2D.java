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

    public double getLength() {
        return Math.sqrt(x*x + y*y);
    }

    public static double getAngle(Vec2D v1, Vec2D v2) {
        double dx = v1.x - v2.x;
        double dy = v1.y - v2.y;
        double inRads = Math.atan2(dy, dx);

        double result = Math.toDegrees(inRads);
        result-=90; // rotate counterclockwise 90 degrees;
        return (result < 0) ? 360d + result : result;
    }

    public static Vec2D diff(Vec2D v1, Vec2D v2) {
        Vec2D result = new Vec2D(v2.x - v1.x, v2.y - v1.y);
        return result;
    }

    public static int distance(Vec2D v1, Vec2D v2) {
        double x = v1.x - v2.x;
        double y = v1.y - v2.y;
        double result = Math.sqrt(x*x + y*y);
        return (int) result;
    }

    public Vec2D subtract(Vec2D v) {
        return new Vec2D(this.x - v.x, this.y - v.y);
    }

    public static Vec2D sum(Vec2D v1, Vec2D v2) {
        Vec2D result = new Vec2D();
        result.x = v1.x + v2.x;
        result.y = v1.y + v2.y;
        return result;
    }

    public Vec2D add(double i) {
        return new Vec2D(x + i, y + i);
    }

    public void normalize(double i) {
        double length = Math.sqrt(x*x + y*y);
        double invLength = i / length;
        this.x *= invLength;
        this.y *= invLength;
    }

    @Override
    public String toString() {
        return "x:" + String.valueOf(this.x) + " y:" + String.valueOf(this.y);
    }

}