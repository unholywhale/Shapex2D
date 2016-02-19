package com.whale.shapex2d.entities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.whale.shapex2d.R;
import com.whale.shapex2d.animations.Animations;
import com.whale.shapex2d.geom.Vec2D;
import com.whale.shapex2d.interfaces.Enemy;
import com.whale.shapex2d.interfaces.Entity;
import com.whale.shapex2d.interfaces.Moving;
import com.whale.shapex2d.interfaces.Projectile;
import com.whale.shapex2d.interfaces.Sensors;
import com.whale.shapex2d.strategies.StandardEnemyStrategy;

import java.util.ArrayList;

/**
 * Enemy tank class
 */
public class Tank implements Moving, Enemy, Sensors {
    public static final int TANK_HEALTH = 20;
    public static final double TANK_RADIUS = 70;
    private final SensorsTask mTask;
    public ArrayList<Entity> mSensorObjects;
    private Entity mSelf = this;
    private Entity mSensorTarget;
    private Vec2D mPosition;
    private Vec2D mNext;
    private Vec2D mVelocity;
    private double mMass;
    private double mRadius;
    private double mHealth;
    private Drawable mChassisDrawable;
    private Drawable mBaseDrawable;
    private Drawable mGunDrawable;
    private Drawable mDrawable;
    private ArrayList<Drawable> mAnimation;
    private int mAnimationCounter = 0;
    private double mGunAngle = 180;
    private boolean isDead;
    private boolean isDelete;

    public Tank(Context context, Vec2D position, Vec2D velocity) {
        Resources res = context.getResources();
        mPosition = position;
        mVelocity = velocity;
        mNext = Vec2D.sum(position, velocity);
        mMass = 100000000;
        mHealth = TANK_HEALTH;
        mRadius = TANK_RADIUS;
        mAnimation = Animations.INSTANCE.getAnimation(Animations.ANIM_CIRCLE_EXPLOSION);
        mChassisDrawable = res.getDrawable(R.drawable.chassis_1);
        mBaseDrawable = res.getDrawable(R.drawable.tankbase_1);
        mGunDrawable = res.getDrawable(R.drawable.tankgun_1);
        mTask = new SensorsTask();
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

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
        mNext = Vec2D.sum(mNext, mVelocity);
        return 0;
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
    public void die() {
        isDead = true;
        mTask.cancel(true);
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    @Override
    public boolean isDelete() {
        return isDelete;
    }

    private void aimAtTarget() {
        if (mSensorTarget != null) {
            double angle = Vec2D.getAngle(mPosition, mSensorTarget.getPosition());
            if (angle < mGunAngle) {
                mGunAngle--;
            } else if (angle > mGunAngle) {
                mGunAngle++;
            }
        }
    }

    public void draw(Canvas canvas) {
        if (!isDead) {
            move();
            aimAtTarget();
            drawTank(canvas);
        } else {
            drawExplosion(canvas);
        }
    }

    private void drawTank(Canvas canvas) {
        int x = (int) mPosition.x;
        int y = (int) mPosition.y;
        int r = (int) mRadius;
        mChassisDrawable.setBounds(x - r, y - r, x + r, y + r);
        mChassisDrawable.draw(canvas);
        mBaseDrawable.setBounds(x - r, y - r, x + r, y + r);
        mBaseDrawable.draw(canvas);
        canvas.save();
        canvas.rotate((float) mGunAngle, x, y);
        mGunDrawable.setBounds(x - r, y - r, x + r, y + r);
        mGunDrawable.draw(canvas);
        canvas.restore();
    }

    private void drawExplosion(Canvas canvas) {
        try {
            mDrawable = mAnimation.get(mAnimationCounter);
            mAnimationCounter++;
        } catch (IndexOutOfBoundsException e) {
            isDelete = true;
            mAnimationCounter = 0;
            return;
        }
        int x = (int) mPosition.x;
        int y = (int) mPosition.y;
        int r = (int) mRadius;
        mDrawable.setBounds(x - r, y - r, x + r, y + r);
        mDrawable.draw(canvas);
    }

    @Override
    public void setSensors(ArrayList<Entity> objects) {
        if (objects != mSensorObjects) {
            mSensorObjects = objects;
        }
    }

    private class SensorsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            while (!this.isCancelled()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e("SENSORS", "INTERRUPTED");
                    e.printStackTrace();
                }
                publishProgress();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            updateTarget();
        }

        private void updateTarget() {
            if (mSensorObjects == null) {
                return;
            }
            mSensorTarget = StandardEnemyStrategy.getTarget(mSensorObjects, mSelf);
        }
    }
}
