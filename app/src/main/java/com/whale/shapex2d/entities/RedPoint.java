package com.whale.shapex2d.entities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.whale.shapex2d.R;
import com.whale.shapex2d.animations.Animations;
import com.whale.shapex2d.enums.Boundaries;
import com.whale.shapex2d.geom.Vec2D;
import com.whale.shapex2d.interfaces.Enemy;
import com.whale.shapex2d.interfaces.Moving;

import java.util.ArrayList;

/**
 * Class for a simple red point
 */
public class RedPoint implements Moving, Enemy {

    public static final int SPEED = 10;

    ///////////////////////////////////////////////////////////////////////////
    // Declarations
    ///////////////////////////////////////////////////////////////////////////
    public static final double POINT_RADIUS = 15;
    public static final int BOUNDARY = 5;
    private double mMass;
    private Vec2D mPosition;
    private double mRadius;
    private Vec2D mVelocity;
    private Vec2D mNext;
    private ArrayList<Drawable> mExplosionAnimation;
    private int mAnimationCounter = 0;
    private Drawable mDrawable;
    private Paint mPaint;
    private Context mContext;
    private boolean isDead = false;
    private boolean isDelete = false;

    public RedPoint(Context context) {
        init(context, new Vec2D(0, 0), new Vec2D(0, 0), POINT_RADIUS, 10);
    }

    public RedPoint(Context context, Vec2D position) {
        init(context, position, new Vec2D(0, 0), POINT_RADIUS, 10);
    }

    public RedPoint(Context context, Vec2D position, Vec2D velocity, int mass) {
        init(context, position, velocity, POINT_RADIUS, mass);
    }

    public RedPoint(Context context, Vec2D position, Vec2D velocity) {
        init(context, position, velocity, POINT_RADIUS, 10);
    }

    public RedPoint(Context context, Vec2D position, Vec2D velocity, double radius) {
        init(context, position, velocity, radius, 10);
    }

    private void init(Context context, Vec2D position, Vec2D velocity, double radius, double mass) {
        mMass = mass;
        mContext = context;
        mRadius = radius;
        mPosition = position;
        mVelocity = velocity;
        mNext = new Vec2D(position.x + velocity.x, position.y + velocity.y);
        try {
            mExplosionAnimation = Animations.INSTANCE.getAnimation(Animations.ANIM_EXPLOSION);
        } catch (Resources.NotFoundException e) {
            Log.e("ANIMATION",  "Animation " + Animations.ANIM_EXPLOSION + " not found");
        }
        mDrawable = context.getResources().getDrawable(R.drawable.point_small);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters and setters
    ///////////////////////////////////////////////////////////////////////////

    public double getMass() {
        return mMass;
    }

    public void setVelocity(Vec2D velocity) {
        this.mVelocity = velocity;
    }

    public Vec2D getVelocity() {
        return mVelocity;
    }

    public Vec2D getPosition() {
        return mPosition;
    }

    public Vec2D getNextPos() {
        return mNext;
    }

    public double getRadius() {
        return mRadius;
    }

    public void setRadius(double mRadius) {
        this.mRadius = mRadius;
    }

    public Drawable getmDrawable() {
        return mDrawable;
    }

    public void setmDrawable(Drawable mDrawable) {
        this.mDrawable = mDrawable;
    }

    public Paint getmPaint() {
        return mPaint;
    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Interface overrides
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public int move() {
        mPosition = mNext;
        mNext = new Vec2D(mPosition.x + mVelocity.x, mPosition.y + mVelocity.y);
        return 0;
    }

    @Override
    public void hit() {
        die();
    }

    @Override
    public void hit(double dmg) {
        die();
    }

    @Override
    public void die() {
        isDead = true;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    @Override
    public boolean isDelete() {
        return isDelete;
    }

    public Boundaries  getBoundary(int xBorder, int yBorder) {
        if (mNext.x <= BOUNDARY)
            return Boundaries.START;
        if (mNext.y <= BOUNDARY)
            return Boundaries.TOP;
        if (mNext.x + mRadius > xBorder - BOUNDARY)
            return Boundaries.END;
        if (mNext.y + mRadius > yBorder - BOUNDARY)
            return Boundaries.BOTTOM;
        return Boundaries.NONE;
    }

    public void deflect(Boundaries b) {
        if (b == Boundaries.TOP) {// || b == Boundaries.BOTTOM) {
            mVelocity.y = -mVelocity.y;
            mNext.y += mVelocity.y;
        } else if (b == Boundaries.START || b == Boundaries.END) {
            mVelocity.x = -mVelocity.x;
            mNext.x += mVelocity.x;
        }
    }

    public void animateExplosion() {
        if (mAnimationCounter < mExplosionAnimation.size()) {
            mDrawable = mExplosionAnimation.get(mAnimationCounter);
            mRadius = 60;
            mAnimationCounter++;
        } else {
            isDelete = true;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isDead) {
            move();
            deflect(getBoundary(canvas.getWidth(), canvas.getHeight()));
        } else {
            animateExplosion();
        }
        int start = (int) (mPosition.x - mRadius);
        int top = (int) (mPosition.y - mRadius);
        int bottom = (int) (mPosition.y + mRadius);
        int end = (int) (mPosition.x + mRadius);
        mDrawable.setBounds(start, top, end, bottom);

        mDrawable.draw(canvas);
    }
}
