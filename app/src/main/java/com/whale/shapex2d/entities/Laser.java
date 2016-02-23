package com.whale.shapex2d.entities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.whale.shapex2d.R;
import com.whale.shapex2d.geom.Vec2D;
import com.whale.shapex2d.interfaces.Beam;
import com.whale.shapex2d.interfaces.Entity;

import java.util.ArrayList;

/**
 * Created by alex on 23/02/16.
 */
public class Laser implements Entity, Beam {

    public static final double BEAM_THICKNESS = 60;
    public static final int BEAM_LIFE = 200;
    private Context mContext;
    private Vec2D mStart;
    private Vec2D mEnd;
    private double mLength;
    private double mAngle;
    private double mThickness;
    private Drawable mDrawable;
    private int mCounter;
    private boolean isDelete;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public Laser(Context context, Vec2D start, Vec2D end) {
        init(context, start, end);
    }

    private void init(Context context, Vec2D start, Vec2D end) {
        mContext = context;
        mStart = start;
        mEnd = end;
        mLength = Vec2D.distance(start, end);
        mAngle = Vec2D.getAngle(start, end) - 90;
        mThickness = BEAM_THICKNESS;
        mDrawable = context.getResources().getDrawable(R.drawable.laser_1);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Overrides
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public Vec2D getStart() {
        return mStart;
    }

    @Override
    public Vec2D getEnd() {
        return mEnd;
    }

    @Override
    public double getThickness() {
        return mThickness;
    }

    @Override
    public Vec2D getPosition() {
        return null;
    }

    @Override
    public Vec2D getNextPos() {
        return null;
    }

    @Override
    public Vec2D getVelocity() {
        return null;
    }

    @Override
    public void setVelocity(Vec2D velocity) {
        // do nothing
    }

    @Override
    public double getMass() {
        return 0;
    }

    @Override
    public double getRadius() {
        return 0;
    }

    @Override
    public void setRadius(double r) {
        // do nothing
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
        // do nothing
    }

    @Override
    public void hit(double dmg) {
        // do nothing
    }

    @Override
    public void die() {
        // do nothing
    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public boolean isDelete() {
        return isDelete;
    }

    @Override
    public void draw(Canvas canvas) {
        mCounter++;
        if (mCounter % BEAM_LIFE == 0) {
            isDelete = true;
        }
        int x = (int) mStart.x;
        int y = (int) mStart.y;
        int t = (int) mThickness;
        int l = (int) mLength;
        mDrawable.setBounds(x, y - t, x + l, y + t);
        canvas.save();
        canvas.rotate((float) mAngle, (float) x, (float) y);
        mDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public ArrayList<Entity> shoot() {
        return null;
    }
}
