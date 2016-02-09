package com.whale.shapex2d.geom;

import android.util.Log;

import com.whale.shapex2d.MainActivity;
import com.whale.shapex2d.interfaces.Movable;
import com.whale.shapex2d.interfaces.Stationary;

public class Collision {
    public Vec2D mContactVector;
    public Vec2D newV1, newV2;
    public Vec2D mDelta = new Vec2D();


    public void setData(Movable m1, Movable m2, Vec2D contactVector) {
        this.mContactVector = contactVector;
        double magnitude = getMagnitude(m1, m2);
        this.newV1 = getNewMoveVec(m1, false, magnitude);
        this.newV2 = getNewMoveVec(m2, true, magnitude);
    }

    public void setData(Movable m1, Stationary s1, Vec2D contactVector) {
        this.mContactVector = contactVector;
        double magnitude = getMagnitude(m1);
        this.newV1 = getNewMoveVec(m1, false, magnitude);
        this.newV2 = new Vec2D(0,0);
    }

    public double getMagnitude(Movable m1, Movable m2) {
        double a1 = getA(m1);
        double a2 = getA(m2);
        double value = 2 * m1.getMass() * m2.getMass() * (a1 - a2) / (m1.getMass() + m2.getMass());
        return Math.abs(value);
    }

    public double getMagnitude(Movable m1) {
        double a1 = getA(m1);
        double value = 2 * m1.getMass() * Integer.MAX_VALUE * a1 / (m1.getMass() + Integer.MAX_VALUE);
        return Math.abs(value);
    }

    public double getA(Movable m) {
        return m.getVelocity().x * mContactVector.x + m.getVelocity().y * mContactVector.y;
    }

    private Vec2D getNewMoveVec(Movable m, boolean negative, double magnitude) {
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
        if (m.getMass() == Integer.MAX_VALUE) {
            mDelta.x = 0;
            mDelta.y = 0;
        }
        newMoveVec.x = moveVec.x + (sign * mDelta.x);
        newMoveVec.y = moveVec.y + (sign * mDelta.y);
        return newMoveVec;
    }
}
