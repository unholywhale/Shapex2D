package com.whale.shapex2d;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.whale.shapex2d.entities.GreenStone;
import com.whale.shapex2d.entities.RedPoint;
import com.whale.shapex2d.geom.Collision;
import com.whale.shapex2d.geom.Vec2D;
import com.whale.shapex2d.interfaces.Movable;
import com.whale.shapex2d.interfaces.Stationary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * An implementation of SurfaceView
 */
public class ShapexView extends SurfaceView implements SurfaceHolder.Callback {

    public static final int RED_SPEED = 10;
    private ShapexThread mThread;
    private Context mContext;
    private ArrayList<Movable> movingObjects = new ArrayList<>();
    private Vector<Stationary> stationaryObjects = new Vector<>();
    private ArrayList<Stationary> growingObjects = new ArrayList<>();

    public ShapexView(Context context) {
        super(context);
        mContext = context;
        getHolder().addCallback(this);
        init();
    }

    public ShapexView(Context context, AttributeSet attrs, ShapexThread thread) {
        super(context, attrs);
        mContext = context;
        getHolder().addCallback(this);
        mThread = thread;
        init();
    }

    public ShapexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        getHolder().addCallback(this);
        init();
    }

    private void init() {
    }

    public void actionDown(Vec2D position) {
        GreenStone stone = (GreenStone) addStationary(position);
        growingObjects.add(stone);
    }

    public void actionUp() {
        GreenStone stone = (GreenStone) growingObjects.get(growingObjects.size() - 1);
        stone.setGrowing(false);
    }

    public static int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    public static Vec2D randomPosition(int rangeX, int rangeY) {
        return new Vec2D(randomWithRange(0, rangeX), randomWithRange(0, rangeY));
    }

    public static Vec2D randomVelocity() {
        return new Vec2D(randomWithRange(-RED_SPEED, RED_SPEED), randomWithRange(-RED_SPEED, RED_SPEED));
    }

    public void addObject(Vec2D position) {
        Vec2D velocity = randomVelocity();
        movingObjects.add(new RedPoint(mContext, position, velocity, 15));
    }

    public Stationary addStationary(Vec2D position) {
        GreenStone greenStone = new GreenStone(mContext, position);
        greenStone.setGrowing(true);
        stationaryObjects.add(greenStone);
        return greenStone;
    }

    public void refresh() {
        mThread.setRunning(false);
        boolean retry = true;
        while (retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mThread = new ShapexThread(mContext, getHolder());
        mThread.setRunning(true);
        mThread.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initScene();
        mThread = new ShapexThread(mContext, getHolder());
        mThread.setRunning(true);
        mThread.start();
    }

    private void initScene() {
        for (int i = 0; i < 100; i++) {
            addObject(ShapexView.randomPosition(getWidth(), getHeight()));
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mThread.setRunning(false);
        boolean retry = true;
        while (retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    class ShapexThread extends Thread {

        private SurfaceHolder mSurfaceHolder;
        private boolean mRunning = false;
        private Context mContext;


        public ShapexThread(Context context, SurfaceHolder holder) {
            this.mSurfaceHolder = holder;
            mContext = context;
//            movingObjects.add(new RedPoint(context, 300, 400, 6, 0, 10));
//            movingObjects.add(new RedPoint(context, 600, 400, -5, 0, 30));
//            movingObjects.add(new RedPoint(context, 10, 40, 2, 3, 20));
//            movingObjects.add(new RedPoint(context, 580, 230, 1, 5, 10));
//            movingObjects.add(new RedPoint(context, 234, 22, 2, 2, 13));
//            movingObjects.add(new RedPoint(context, 124, 65, -2, -3, 14));
//            movingObjects.add(new RedPoint(context, 333, 233, -2, 4, 25));
//            movingObjects.add(new RedPoint(context, 1000, 800, -6, 4, 50));
        }

        public void setRunning(boolean running) {
            this.mRunning = running;
        }

        @Override
        public void run() {
            while (mRunning) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);

                    if (c == null) continue;
                    draw(c);
                } finally {
                    if (c != null) mSurfaceHolder.unlockCanvasAndPost(c);
                }

            }
            super.run();
        }

        private boolean hasCollision(Movable m1, Movable m2) {
            Vec2D nextCenter1 = m1.getNextPos().add(m1.getRadius());
            Vec2D nextCenter2 = m2.getNextPos().add(m2.getRadius());
            double distance = Vec2D.distance(nextCenter1, nextCenter2) - m1.getRadius() - m2.getRadius();
            return distance <= 0;
        }

        private Vec2D getContactVector(Movable m1, Movable m2) {
            Vec2D contact = m1.getPosition().subtract(m2.getPosition());
            contact.normalize(1);
            return contact;
        }

        private Collision collision = new Collision();
        private void draw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);
            Movable m1;
            Movable m2;
            for (int i = 0; i < movingObjects.size(); i++) {
                m1 = movingObjects.get(i);
                for (int j = i + 1; j < movingObjects.size(); j++) {
                    m2 = movingObjects.get(j);
                    if (!hasCollision(m1, m2)) {
                        continue;
                    }
                    Vec2D contactVector = getContactVector(m1, m2);
                    collision.setData(m1, m2, contactVector);
                    m1.setVelocity(collision.newV1);
                    m2.setVelocity(collision.newV2);

                }
//                Log.d("POSITION", m1.getPosition().toString());
//                Log.d("VELOCITY", m1.getVelocity().toString());
//                Log.d("NEXT", m1.getNextPos().toString());
                m1.draw(canvas);
            }

            synchronized (stationaryObjects) {
                for (Stationary s : stationaryObjects) {
                    s.draw(canvas);
                }
            }
        }

    }
}
