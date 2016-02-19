package com.whale.shapex2d.entities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.whale.shapex2d.R;
import com.whale.shapex2d.geom.Vec2D;
import com.whale.shapex2d.interfaces.Moving;
import com.whale.shapex2d.interfaces.Stationary;

/**
 * Game base object
 */
public class Base implements Stationary {
    public static final int BASE_HEALTH = 100;
    private Context mContext;
    private Drawable mDrawable;
    private Vec2D mPosition;
    private double mMass;
    private double mRadius;
    private int mHealth;
    private Vec2D mGunPosition;
    private boolean isDead;
    private boolean isDelete;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////
    public Base(Context context, Vec2D position, double radius) {
        mContext = context;
        mPosition = position;
        mRadius = radius;
        mHealth = BASE_HEALTH;
        mMass = Integer.MAX_VALUE;
        mDrawable = context.getResources().getDrawable(R.drawable.greenpoint_small);
    }

    @Override
    public Vec2D getPosition() {
        return mPosition;
    }

    @Override
    public Vec2D getVelocity() {
        return new Vec2D(0, 0);
    }

    @Override
    public double getMass() {
        return mMass;
    }

    @Override
    public double getRadius() {
        return mRadius;
    }

    @Override
    public void setVelocity(Vec2D velocity) {
        // do nothing
    }

    public int getHealth() {
        return mHealth;
    }

    @Override
    public boolean isDelete() {
        return isDelete;
    }

    @Override
    public void cancel() {

    }

    @Override
    public void action(Vec2D position) {

    }

    @Override
    public Moving shoot() {
        return null;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    @Override
    public void hit() {
        hit(1);
    }

    @Override
    public void hit(double dmg) {
        mHealth -= dmg;
        if (mHealth <= 0) {
            die();
        }
    }

    @Override
    public Vec2D getNextPos() {
        return mPosition;
    }

    @Override
    public void die() {
        mDrawable = mContext.getResources().getDrawable(R.drawable.point_small);
        isDead = true;
    }

    @Override
    public void draw(Canvas canvas, @Nullable Vec2D currentTouch) {
        draw(canvas);
    }

    @Override
    public void draw(Canvas canvas) {
        int x = (int) mPosition.x;
        int y = (int) mPosition.y;
        int r = (int) mRadius;
        mDrawable.setBounds(x - r, y - r, x + r, y + r);
        mDrawable.draw(canvas);
    }
}
