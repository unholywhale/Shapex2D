package com.whale.shapex2d.entities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.whale.shapex2d.R;
import com.whale.shapex2d.animations.Animations;
import com.whale.shapex2d.geom.Vec2D;
import com.whale.shapex2d.interfaces.Moving;
import com.whale.shapex2d.interfaces.Weapon;

import java.util.ArrayList;

/**
 * A bullet object
 */
public class Bullet implements Moving, Weapon {
    ///////////////////////////////////////////////////////////////////////////
    // Declarations
    ///////////////////////////////////////////////////////////////////////////
    public static final double BULLET_RADIUS = 5;
    public static final int BOUNDARY = 5;
    private double mMass;
    private Vec2D mPosition;
    private double mRadius;
    private Vec2D mVelocity;
    private Vec2D mNext;
    private int mAnimationCounter = 0;
    private ArrayList<Drawable> mExplosionAnimation;
    private Drawable mDrawable;
    private Paint mPaint;
    private Context mContext;
    private boolean isDead = false;
    private boolean isDelete = false;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////
    public Bullet(Context context) {
        init(context, new Vec2D(0, 0), new Vec2D(0, 0), BULLET_RADIUS, 1);
    }

    public Bullet(Context context, Vec2D position) {
        init(context, position, new Vec2D(0, 0), BULLET_RADIUS, 1);
    }

    public Bullet(Context context, Vec2D position, Vec2D velocity, int mass) {
        init(context, position, velocity, BULLET_RADIUS, mass);
    }

    public Bullet(Context context, Vec2D position, Vec2D velocity) {
        init(context, position, velocity, BULLET_RADIUS, 1);
    }

    public Bullet(Context context, Vec2D position, Vec2D velocity, double radius) {
        init(context, position, velocity, radius, 1);
    }

    private void init(Context context, Vec2D position, Vec2D velocity, double radius, int mass) {
        mContext = context;
        mPosition = position;
        mVelocity = velocity;
        mNext = Vec2D.sum(mPosition, mVelocity);
        mRadius = radius;
        mMass = mass;
        try {
            mExplosionAnimation = Animations.INSTANCE.getAnimation(Animations.ANIM_EXPLOSION);
        } catch (Resources.NotFoundException e) {
            Log.e("ANIMATION",  "Animation " + Animations.ANIM_EXPLOSION + " not found");
        }
        mDrawable = context.getResources().getDrawable(R.drawable.bullet_1);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Interface overrides
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public Vec2D getNextPos() {
        return mNext;
    }

    @Override
    public Vec2D getPosition() {
        return mPosition;
    }

    @Override
    public Vec2D getVelocity() {
        return mVelocity;
    }

    @Override
    public double getMass() {
        return mMass;
    }

    @Override
    public void setVelocity(Vec2D velocity) {
        mVelocity = velocity;
    }

    @Override
    public void setRadius(double r) {
        mRadius = r;
    }

    @Override
    public double getRadius() {
        return mRadius;
    }

    @Override
    public int move() {
        mPosition = mNext;
        mNext = Vec2D.sum(mPosition, mVelocity);
        return 0;
    }

    @Override
    public void hit() {
        die();
    }

    @Override
    public void hit(double dmg) {
        hit();
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

    private void checkBounds(int x, int y) {
        if (mPosition.x < 0
                || mPosition.y < 0
                || mPosition.x > x
                || mPosition.y > y) {
            isDelete = true;
        }
    }

    public void animateExplosion() {
        if (mAnimationCounter < mExplosionAnimation.size()) {
            mDrawable = mExplosionAnimation.get(mAnimationCounter);
            mRadius = 30;
            mAnimationCounter++;
        } else {
            isDelete = true;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        checkBounds(canvas.getWidth(), canvas.getHeight());
        if (!isDead) {
            move();
        } else {
            animateExplosion();
        }
        int x = (int) mPosition.x;
        int y = (int) mPosition.y;
        int r = (int) mRadius;
        mDrawable.setBounds(x - r, y - r, x + r, y + r);
        mDrawable.draw(canvas);
    }
}
