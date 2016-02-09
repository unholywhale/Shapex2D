package com.whale.shapex2d.entities;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;

import com.whale.shapex2d.R;
import com.whale.shapex2d.geom.Vec2D;
import com.whale.shapex2d.interfaces.Stationary;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class GreenStone implements Stationary {

    public static final double DEFAULT_RADIUS = 10;
    private boolean mGrowing = false;
    private Vec2D mPosition;
    private double mRadius;
    private Context mContext;
    private Bitmap mBitmap;
    private Drawable mDrawable;

    public GreenStone(Context context) {
        init(context, new Vec2D(0, 0), DEFAULT_RADIUS);
    }

    public GreenStone(Context context, Vec2D position) {
        init(context, position, DEFAULT_RADIUS);
    }

    public void grow() {
        mRadius+=1;
    }

    private void init(Context context, Vec2D position, double radius) {
        mContext = context;
        mPosition = position;
        mRadius = radius;
        mDrawable = context.getResources().getDrawable(R.drawable.greenpoint_small, null);
    }

    @Override
    public Vec2D getPosition() {
        return mPosition;
    }

    @Override
    public double getRadius() {
        return mRadius;
    }

    public void setGrowing(boolean isGrowing) {
        mGrowing = isGrowing;
    }

    public boolean isGrowing() {
        return mGrowing;
    }

    public void die() {
        mDrawable = mContext.getResources().getDrawable(R.drawable.point_small, null);
    }

    @Override
    public void draw(Canvas canvas) {
            if (mGrowing) {
                grow();
            }

        int x = (int) mPosition.x;
        int y = (int) mPosition.y;
        int r = (int) mRadius;
        mDrawable.setBounds(x - r, y - r, x + r, y + r);
        mDrawable.draw(canvas);
    }
}
