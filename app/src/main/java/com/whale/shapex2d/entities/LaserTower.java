package com.whale.shapex2d.entities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.whale.shapex2d.R;
import com.whale.shapex2d.animations.Animations;
import com.whale.shapex2d.geom.Vec2D;
import com.whale.shapex2d.interfaces.Entity;
import com.whale.shapex2d.interfaces.Friend;

import java.util.ArrayList;

/**
 * Tower that shoots laser
 */
public class LaserTower implements Entity, Friend {

    public static final double DEFAULT_RADIUS = 70;
    public static final int TOWER_HEALTH = 20;
    public static final int AMMO = 1;
    public static final int AMMO_FREQ = 5; // in frames
    public static final int PLATFORM_IMG = R.drawable.platform_1;
    public static final int BASE_IMG = R.drawable.base_1;
    public static final int GUN_IMG = R.drawable.gun_2;
    private boolean isDead = false;
    //private boolean isVulnerable = true;
    private int mHealth = TOWER_HEALTH;
    private double mMass;
    private Vec2D mPosition;
    private double mRadius;
    private Context mContext;
    private Drawable mDrawable;
    private Drawable mChargeDrawable;
    private Drawable mPlatformDrawable;
    private Drawable mBaseDrawable;
    private Drawable mGunDrawable;
    private Vec2D mGunPosition;
    private int mAmmo = AMMO;
    private int mGunCounter = 0;
    private float mGunAngle = 0;
    private final int CHARGE_FREQ = 400;
    private int mChargeCounter = 0;
    private ArrayList<Drawable> mBlinkAnimation;
    private ArrayList<Drawable> mAnimation;
    private int mAnimationCounter = 0;
    private boolean isDelete;
    private boolean isCharged;
    private boolean isShooting;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////
    public LaserTower(Context context, Vec2D position) {
        init(context, position, DEFAULT_RADIUS);
    }

    private void init(Context context, Vec2D position, double radius) {
        mContext = context;
        mPosition = position;
        mRadius = radius;
        mAnimation = Animations.INSTANCE.getAnimation(Animations.ANIM_CIRCLE_EXPLOSION);
        mPlatformDrawable = context.getResources().getDrawable(PLATFORM_IMG);
        mBaseDrawable = context.getResources().getDrawable(BASE_IMG);
        mGunDrawable = context.getResources().getDrawable(GUN_IMG);
        isCharged = true;
    }

    @Override
    public Vec2D getPosition() {
        return mPosition;
    }

    @Override
    public Vec2D getNextPos() {
        return mPosition;
    }

    @Override
    public Vec2D getVelocity() {
        return new Vec2D(0, 0);
    }

    @Override
    public void setVelocity(Vec2D velocity) {
        // do nothing
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
    public void setRadius(double r) {
        mRadius = r;
    }

    @Override
    public boolean isStationary() {
        return true;
    }

    @Override
    public int move() {
        // do nothing
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
        mAnimationCounter = 0;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    @Override
    public boolean isDelete() {
        return isDelete;
    }

    @Override
    public ArrayList<Entity> shoot() {
        if (isShooting) {
            isCharged = false;
            if (mAmmo > 0) {
                mAmmo--;
                Laser laser = new Laser(mContext, mPosition, mGunPosition);
                ArrayList<Entity> l = new ArrayList<>();
                l.add(laser);
                return l;
            } else {
                mGunCounter = 0;
                isShooting = false;
                mAmmo = AMMO;
            }
        }
        return null;
    }

    @Override
    public void action(Vec2D position) {
        mGunPosition = position;
        mGunAngle = (float) Vec2D.getAngle(mPosition, position);
        if (isCharged) {
            isShooting = true;
        }
    }

    @Override
    public boolean isCharged() {
        return isCharged;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isDead) {
            drawCharge(canvas);
            drawTower(canvas);
        } else {
            drawExplosion(canvas);

        }
    }

    private void charge() {
        if (!isCharged) {
            mChargeCounter++;
            if (mChargeCounter % CHARGE_FREQ == 0) {
                isCharged = true;
                mChargeCounter = 0;
            }
        }
    }

    private void drawCharge(Canvas canvas) {
        int freq = CHARGE_FREQ / 4;
        if (isCharged) {
            mChargeDrawable = mContext.getResources().getDrawable(R.drawable.charge_bar_1);
        } else if (mChargeCounter == 0) {
            mChargeDrawable = null;
        } else if (mChargeCounter % (freq*3) == 0) {
            mChargeDrawable = mContext.getResources().getDrawable(R.drawable.charge_bar_2);
        } else if (mChargeCounter % (freq*2) == 0) {
            mChargeDrawable = mContext.getResources().getDrawable(R.drawable.charge_bar_3);
        } else if (mChargeCounter % freq == 0) {
            mChargeDrawable = mContext.getResources().getDrawable(R.drawable.charge_bar_4);
        }
        if (mChargeDrawable != null) {
            int x = (int) mPosition.x;
            int y = (int) mPosition.y;
            int r = (int) mRadius + 30;
            mChargeDrawable.setBounds(x - r, y - r, x + r, y + r);
            mChargeDrawable.draw(canvas);
        }
        charge();
    }

    private void drawTower(Canvas canvas) {
        int x = (int) mPosition.x;
        int y = (int) mPosition.y;
        int r = (int) mRadius;
        mPlatformDrawable.setBounds(x - r, y - r, x + r, y + r);
        mPlatformDrawable.draw(canvas);
        mBaseDrawable.setBounds(x - r, y - r, x + r, y + r);
        mBaseDrawable.draw(canvas);
        canvas.save();
        canvas.rotate(mGunAngle, x, y);
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
}
