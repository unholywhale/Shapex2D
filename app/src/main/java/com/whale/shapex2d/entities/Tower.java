package com.whale.shapex2d.entities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;

import com.whale.shapex2d.R;
import com.whale.shapex2d.animations.Animations;
import com.whale.shapex2d.animations.BlinkAnimation;
import com.whale.shapex2d.geom.Vec2D;
import com.whale.shapex2d.interfaces.Moving;
import com.whale.shapex2d.interfaces.Stationary;

import java.util.ArrayList;

public class Tower implements Stationary {

    public static final double DEFAULT_RADIUS = 70;
    public static final int TOWER_HEALTH = 5;
    public static final int AMMO = 20;
    public static final int AMMO_FREQ = 25; // in frames
    public static final int PLATFORM_IMG = R.drawable.platform_1;
    public static final int BASE_IMG = R.drawable.base_1;
    public static final int GUN_IMG = R.drawable.gun_1;
    private boolean isDead = false;
    //private boolean isVulnerable = true;
    private int mHealth = TOWER_HEALTH;
    private Vec2D mPosition;
    private double mRadius;
    private Context mContext;
    private Drawable mDrawable;
    private Drawable mPlatformDrawable;
    private Drawable mBaseDrawable;
    private Drawable mGunDrawable;
    private Vec2D mGunPosition;
    private int mAmmo = AMMO;
    private int mGunCounter = 0;
    private ArrayList<Drawable> mBlinkAnimation;
    private ArrayList<Drawable> mAnimation;
    //private boolean isBlinking = false;
    private int mAnimationCounter = 0;
    private boolean isDelete;
    private float mAngle = 0;
    private boolean isAim;
    private boolean isPrepared;

    public Tower(Context context) {
        init(context, new Vec2D(0, 0), DEFAULT_RADIUS);
    }

    public Tower(Context context, Vec2D position) {
        init(context, position, DEFAULT_RADIUS);
    }

    public void grow() {
        mRadius+=1;
    }

    private void init(Context context, Vec2D position, double radius) {
        mContext = context;
        mPosition = position;
        mRadius = radius;
        mAnimation = Animations.INSTANCE.getAnimation(Animations.ANIM_CIRCLE_EXPLOSION);
        mDrawable = getResourceDrawable(R.drawable.graycircle);
        mPlatformDrawable = context.getResources().getDrawable(PLATFORM_IMG);
        mBaseDrawable = context.getResources().getDrawable(BASE_IMG);
        mGunDrawable = context.getResources().getDrawable(GUN_IMG);
        mGunPosition = new Vec2D(0, -mRadius);
        isAim = true;
    }

    private Drawable getResourceDrawable(int id) {
        Drawable d;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            d = mContext.getResources().getDrawable(id, null);
        } else {
            d = mContext.getResources().getDrawable(id);
        }
        return d;
    }

    @Override
    public Vec2D getPosition() {
        return mPosition;
    }



    @Override
    public double getRadius() {
        return mRadius;
    }

    @Override
    public boolean isDelete() {
        return isDelete;
    }

    @Override
    public void hit() {
        mHealth--;
        if (mHealth == 0) {
            die();
        }
    }

    private Vec2D getAmmoPosition() {
        Vec2D position = Vec2D.diff(mPosition, mGunPosition);
        position.normalize(mRadius + 20);
        position = Vec2D.sum(position, mPosition);
        return position;
    }

    @Override
    public void action(Vec2D position) {
        mGunPosition = position;
        mAngle = (float) Vec2D.getAngle(mPosition, position);
        isPrepared = true;
    }

    @Override
    public Moving shoot() {
        if (!isPrepared) return null;
        if (mAmmo == 0) {
            return null;
        }
        mGunCounter++;
        if (mGunCounter % AMMO_FREQ == 0) {
            mAmmo--;
            Vec2D position = getAmmoPosition();
            Vec2D velocity = Vec2D.diff(mPosition, position);
            velocity.normalize(mRadius / 10);
            Bullet b = new Bullet(mContext, position, velocity);
            return b;
        }
        return null;
    }

    public void die() {
        isDead = true;
        mAnimationCounter = 0;
    }

    public void cancel() {
        isAim = false;
    }

    @Override
    public void draw(Canvas canvas) {
        draw(canvas, null);
    }

    @Override
    public void draw(Canvas canvas, @Nullable Vec2D currentTouch) {
        if (!isDead) {
            drawTower(canvas, currentTouch);
        } else {
            drawExplosion(canvas);

        }
    }

    private void drawTower(Canvas canvas, @Nullable Vec2D currentTouch) {
        int x = (int) mPosition.x;
        int y = (int) mPosition.y;
        int r = (int) mRadius;
        mPlatformDrawable.setBounds(x - r, y - r, x + r, y + r);
        mPlatformDrawable.draw(canvas);
        mBaseDrawable.setBounds(x - r, y - r, x + r, y + r);
        mBaseDrawable.draw(canvas);
        if (isAim && currentTouch != null) {
            mAngle = (float) Vec2D.getAngle(mPosition, currentTouch);
            mGunPosition = currentTouch;
        }
        canvas.save();
        canvas.rotate(mAngle, x, y);
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
