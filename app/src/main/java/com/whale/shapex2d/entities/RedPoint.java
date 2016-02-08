package com.whale.shapex2d.entities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import com.whale.shapex2d.R;
import com.whale.shapex2d.enums.Boundaries;
import com.whale.shapex2d.geom.Vec2D;
import com.whale.shapex2d.interfaces.Movable;

/**
 * Class for a simple red point
 */
public class RedPoint implements Movable {

    ///////////////////////////////////////////////////////////////////////////
    // Declarations
    ///////////////////////////////////////////////////////////////////////////
    public static final int POINT_RADIUS = 10;
    public static final int BOUNDARY = 0;
    private Vec2D mPosition;
    private Vec2D mCenter;
    private int mRadius;
    private Vec2D mVelocity;
    private Vec2D mNext;
    private Drawable mDrawable;
    private Paint mPaint;
    private Context mContext;

    public RedPoint(Context context) {
        init(context, 0, 0, 0, 0, POINT_RADIUS);
    }

    public RedPoint(Context context, int x, int y) {
        init(context, x, y, 0, 0, POINT_RADIUS);
    }

    public RedPoint(Context context, int x, int y, int velX, int velY) {
        init(context, x, y, velX, velY, POINT_RADIUS);
    }

    public RedPoint(Context context, int x, int y, int velX, int velY, int radius) {
        init(context, x, y, velX, velY, radius);
    }

    private void init(Context context, int x, int y, int velX, int velY, int radius) {
        mContext = context;
        mRadius = radius;
        mPosition = new Vec2D(x, y);
        mCenter = new Vec2D(x + radius, y + radius);
        mVelocity = new Vec2D(velX, velY);
        mNext = new Vec2D(mPosition.x + mVelocity.x, mPosition.y + mVelocity.y);
        mDrawable = context.getResources().getDrawable(R.drawable.point);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters and setters
    ///////////////////////////////////////////////////////////////////////////

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

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int mRadius) {
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
    public int move(int velX, int velY) {
        mPosition = mNext;
        return 0;
    }

    @Override
    public boolean intersects(int x, int y) {
        return false;
    }

    @Override
    public Boundaries getBoundary(int xBorder, int yBorder) {
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

    @Override
    public void disappear() {

    }

    public void deflect(Boundaries b) {
        if (b == Boundaries.TOP || b == Boundaries.BOTTOM) {
            mVelocity.y = -mVelocity.y;
            mNext.y += mVelocity.y;
        } else if (b == Boundaries.START || b == Boundaries.END) {
            mVelocity.x = -mVelocity.x;
            mNext.x += mVelocity.x;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        move();
        deflect(getBoundary(canvas.getWidth(), canvas.getHeight()));
        mDrawable.setBounds((int) mPosition.x, (int) mPosition.y, (int) mPosition.x + mRadius*2, (int) mPosition.y + mRadius*2);
        mDrawable.draw(canvas);
    }
}
