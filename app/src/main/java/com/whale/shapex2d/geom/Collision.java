package com.whale.shapex2d.geom;

import android.util.Log;

import com.whale.shapex2d.interfaces.Beam;
import com.whale.shapex2d.interfaces.Entity;
import com.whale.shapex2d.interfaces.Moving;
import com.whale.shapex2d.interfaces.Stationary;

public class Collision {
    public Vec2D mContactVector;
    public Vec2D newV1, newV2;
    public Vec2D mDelta = new Vec2D();

    public static boolean hasCollision(Entity e1, Entity e2) {
        if (e2 instanceof Beam) {
            Beam b = (Beam) e2;
            return distanceTo(e1, b) <= e1.getRadius() + b.getThickness();
        } else {
            double distance = Vec2D.distance(e1.getNextPos(), e2.getNextPos()) - e1.getRadius() - e2.getRadius();
            return distance <= 0;
        }
    }

    public static double distanceTo(Entity e, Beam b) {
        double distance;
        boolean isValidNormal = hasNormal(e.getPosition(), b.getStart(), b.getEnd());
        if (isValidNormal) {
            distance = getNormalDistance(e, b);
        } else {
            double d1 = Vec2D.distance(e.getPosition(), b.getStart());
            double d2 = Vec2D.distance(e.getPosition(), b.getEnd());
            distance = d1 < d2 ? d1 : d2;
        }
        return distance;
    }

    public static double getNormalDistance(Entity e, Beam b) {
        Vec2D start = b.getStart();
        Vec2D end = b.getEnd();
        Vec2D pos = e.getPosition();
        Vec2D vec = Vec2D.diff(start, end);
        double numerator = (start.y - end.y) * pos.x + vec.x * pos.y + (start.x * end.y - end.x * start.y);
        double denominator = Math.sqrt(vec.x*vec.x + vec.y*vec.y);
        double distance = Math.abs(numerator/denominator);
        return distance;
    }

    public static boolean hasNormal(Vec2D point, Vec2D start, Vec2D end) {
        double segLen = Vec2D.distance(start, end);
        double v1Len = Vec2D.distance(point, start);
        if (v1Len > segLen) {
            return false;
        }
        double v2Len = Vec2D.distance(point, end);
        if (v2Len > segLen) {
            return false;
        }
        return true;
    }

    public static Vec2D getContactVector(Entity e1, Entity e2) {
        Vec2D contact = e1.getPosition().subtract(e2.getPosition());
        contact.normalize(1);
        return contact;
    }

    public void setData(Entity e1, Entity e2, Vec2D contactVector) {
        this.mContactVector = contactVector;
        double magnitude = getMagnitude(e1, e2);
        this.newV1 = getNewVelocity(e1, false, magnitude);
        this.newV2 = getNewVelocity(e2, true, magnitude);
    }

    public double getMagnitude(Entity e1, Entity e2) {
        double a1 = getA(e1);
        double a2 = getA(e2);
        double value = 2 * e1.getMass() * e2.getMass() * (a1 - a2) / (e1.getMass() + e2.getMass());
        return Math.abs(value);
    }

    public double getA(Entity e) {
        return e.getVelocity().x * mContactVector.x + e.getVelocity().y * mContactVector.y;
    }

    private Vec2D getNewVelocity(Entity e, boolean negative, double magnitude) {
        if (e.getMass() == Integer.MAX_VALUE) { // max mass, always stationary
            return new Vec2D(0, 0);
        }
        Vec2D velocity = e.getVelocity();
        Vec2D newVelocity = new Vec2D();
        int sign = 1;
        if (negative) {
            sign = -1;
        }
        mDelta.x = mContactVector.x;
        mDelta.y = mContactVector.y;
        mDelta.normalize(magnitude / e.getMass());
        newVelocity.x = velocity.x + (sign * mDelta.x);
        newVelocity.y = velocity.y + (sign * mDelta.y);
        return newVelocity;
    }
}
