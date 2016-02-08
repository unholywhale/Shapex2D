package com.whale.shapex2d.geom;

import com.whale.shapex2d.interfaces.Movable;

public class Collision {
    public Vec2D mContactVector;
    public Vec2D newV1, newV2;
    public Vec2D mDelta = new Vec2D();


    public void setData(Movable m1, Movable m2, Vec2D contactVector) {
        this.mContactVector = contactVector;
        this.newV1 = getNewMoveVec(m1, false);
        this.newV2 = getNewMoveVec(m2, true);
    }

    private Vec2D getNewMoveVec(Movable m, boolean negative) {
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
        mDelta.normalize(1);
        newMoveVec.x = moveVec.x + (sign * mDelta.x);
        newMoveVec.y = moveVec.y + (sign * mDelta.y);
        return newMoveVec;
    }
}
