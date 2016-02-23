package com.whale.shapex2d.entities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.whale.shapex2d.R;
import com.whale.shapex2d.animations.Animations;
import com.whale.shapex2d.geom.Vec2D;
import com.whale.shapex2d.interfaces.Enemy;
import com.whale.shapex2d.interfaces.Entity;
import com.whale.shapex2d.interfaces.Moving;
import com.whale.shapex2d.interfaces.Sensors;

import java.util.ArrayList;

/**
 * Enemy tank class
 */
public class Tank implements Entity, Enemy, Sensors {
    public static final int TANK_HEALTH = 20;
    public static final double TANK_RADIUS = 70;
    public static final int REFRESH_RATE = 50;
    private Context mContext;
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
    private Vec2D mGun1Position;
    private Vec2D mGun2Position;
    private double mGunAngle = 180;
    private boolean isDead;
    private boolean isDelete;
    private boolean isPrepared;
    private final int CHARGE_FREQ = 150;
    private int mChargeCounter = 0;
    private final int AMMO_FREQ = 10;
    private boolean isShooting = false;
    private int mGunCounter = 0;
    private int mAmmo;
    private int mRefreshCounter = 0;
    private boolean isStop = false;

    public Tank(Context context, Vec2D position, Vec2D velocity) {
        mContext = context;
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
//        mTask = new SensorsTask();
//        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
    public boolean isStationary() {
        return false;
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

    private Vec2D getGunPosition(double shift) {
        Vec2D gun = Vec2D.diff(new Vec2D(shift, 0), mPosition);
        gun.rotate(mPosition, mGunAngle);
        return gun;
    }

    private Vec2D getAmmoPosition(Vec2D gun) {
        Vec2D position = Vec2D.sum(gun, new Vec2D(0, -mRadius - 10));
        position.rotate(gun, mGunAngle);
        return position;
    }

    @Override
    public ArrayList<Entity> shoot() {
//        if (!isPrepared) {
//            return null;
//        }
        if (!isShooting) {
            mChargeCounter++;
            if (mChargeCounter % CHARGE_FREQ == 0) {
                isShooting = true;
                mAmmo = 3;
                mChargeCounter = 0;
            }
        } else {
            mGunCounter++;
            if (mGunCounter % AMMO_FREQ == 0) {
                if (mAmmo > 0) {
                    mGun1Position = getGunPosition(30);
                    mGun2Position = getGunPosition(-30);
                    Vec2D ammo1 = getAmmoPosition(mGun1Position);
                    Vec2D ammo2 = getAmmoPosition(mGun2Position);
                    Vec2D velocity = Vec2D.rotate(new Vec2D(0, 0), new Vec2D(0, -10), mGunAngle);
                    Bullet bullet1 = new Bullet(mContext, ammo1, velocity);
                    Bullet bullet2 = new Bullet(mContext, ammo2, velocity);
                    ArrayList<Entity> bullets = new ArrayList<>();
                    bullets.add(bullet1);
                    bullets.add(bullet2);
                    mAmmo--;
                    mGunCounter = 0;
                    return bullets;
                } else {
                    isShooting = false;
                }
            }
        }
        return null;
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
            if (mRefreshCounter % REFRESH_RATE == 0) {
                if (mSensorTarget != null) {
                    if (Vec2D.distance(mPosition, mSensorTarget.getPosition()) <= 600) {
                        isStop = true;
                    } else {
                        isStop = false;
                    }
                } else {
                    isStop = false;
                }
                mRefreshCounter = 0;
            }
            if (!isStop) {
                move();
            }
            aimAtTarget();
            drawTank(canvas);
        } else {
            drawExplosion(canvas);
        }
        mRefreshCounter++;
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
    public void setSensorTarget(Entity e) {
        mSensorTarget = e;
    }
}
