package com.whale.shapex2d.geom;

import android.util.Log;

import com.whale.shapex2d.interfaces.Moving;
import com.whale.shapex2d.interfaces.Stationary;

public class Collision {
    public Vec2D mContactVector;
    public Vec2D newV1, newV2;
    public Vec2D mDelta = new Vec2D();

    public static boolean hasCollision(Moving m1, Moving m2) {
        double distance = Vec2D.distance(m1.getNextPos(), m2.getNextPos()) - m1.getRadius() - m2.getRadius();
        return distance <= 0;
    }

    public static boolean hasCollision(Moving m1, Stationary s1) {
        double distance = Vec2D.distance(m1.getNextPos(), s1.getPosition()) - m1.getRadius() - s1.getRadius();
        return distance <= 0;
    }

    public static Vec2D getContactVector(Moving m1, Moving m2) {
        Vec2D contact = m1.getPosition().subtract(m2.getPosition());
        contact.normalize(1);
        return contact;
    }

    public static Vec2D getContactVector(Moving m1, Stationary s1) {
        Vec2D contact = m1.getPosition().subtract(s1.getPosition());
        contact.normalize(1);
        return contact;
    }


    public void setData(Moving m1, Moving m2, Vec2D contactVector) {
        this.mContactVector = contactVector;
        double magnitude = getMagnitude(m1, m2);
        this.newV1 = getNewMoveVec(m1, false, magnitude);
        this.newV2 = getNewMoveVec(m2, true, magnitude);
    }

    public void setData(Moving m1, Vec2D contactVector) {
        this.mContactVector = contactVector;
        double magnitude = getMagnitude(m1);
        this.newV1 = getNewStatVec(m1, magnitude);
        this.newV2 = new Vec2D(0,0);
    }

    public double getMagnitude(Moving m1, Moving m2) {
        double a1 = getA(m1);
        double a2 = getA(m2);
        double value = 2 * m1.getMass() * m2.getMass() * (a1 - a2) / (m1.getMass() + m2.getMass());
        return Math.abs(value);
    }

    public double getMagnitude(Moving m1) {
        double a1 = getA(m1);
        double value = 2 * m1.getMass() * Integer.MAX_VALUE * a1 / (m1.getMass() + Integer.MAX_VALUE);
        return Math.abs(value);
    }

    public double getA(Moving m) {
        return m.getVelocity().x * mContactVector.x + m.getVelocity().y * mContactVector.y;
    }

    private Vec2D getNewStatVec(Moving m, double magnitude) {
        Vec2D moveVec = m.getVelocity();
        Vec2D newMoveVec = new Vec2D();
        double length = moveVec.getLength();
        double energy = (m.getMass() * length*length) / 2;
        mDelta.x = mContactVector.x;
        mDelta.y = mContactVector.y;
        mDelta.normalize(magnitude / m.getMass());
        newMoveVec.x = moveVec.x + mDelta.x;
        newMoveVec.y = moveVec.y + mDelta.y;
        double newlength = newMoveVec.getLength();
        double newenergy = (m.getMass() * newlength*newlength) / 2;
        if (newenergy - 0.1 > energy || newenergy + 0.1 < energy) {
            newMoveVec.normalize(length);
            Log.d("WARNING", "ACHTUNG");
        }
        return newMoveVec;
    }

    private Vec2D getNewMoveVec(Moving m, boolean negative, double magnitude) {
        Vec2D moveVec = m.getVelocity();
        Vec2D newMoveVec = new Vec2D();
        int sign;
        if (negative) {
            sign = -1;
        } else {
            sign = 1;
        }
        mDelta.x = mContactVector.x;
        mDelta.y = mContactVector.y;
        mDelta.normalize(magnitude / m.getMass());
        newMoveVec.x = moveVec.x + (sign * mDelta.x);
        newMoveVec.y = moveVec.y + (sign * mDelta.y);
        return newMoveVec;
    }
}
