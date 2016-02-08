package com.whale.shapex2d;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.whale.shapex2d.entities.RedPoint;
import com.whale.shapex2d.geom.Collision;
import com.whale.shapex2d.geom.Vec2D;
import com.whale.shapex2d.interfaces.Movable;

import java.util.ArrayList;

/**
 * An implementation of SurfaceView
 */
public class ShapexView extends SurfaceView implements SurfaceHolder.Callback {

    private ShapexThread mThread;
    private Context mContext;

    public ShapexView(Context context) {
        super(context);
        mContext = context;
        getHolder().addCallback(this);
    }

    public ShapexView(Context context, AttributeSet attrs, ShapexThread mThread) {
        super(context, attrs);
        mContext = context;
        getHolder().addCallback(this);
        this.mThread = mThread;
    }

    public ShapexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        getHolder().addCallback(this);
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
        mThread = new ShapexThread(mContext, getHolder());
        mThread.setRunning(true);
        mThread.start();
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
        private ArrayList<Movable> movingObjects = new ArrayList<>();

        public ShapexThread(Context context, SurfaceHolder holder) {
            this.mSurfaceHolder = holder;
            mContext = context;
            movingObjects.add(new RedPoint(context, 300, 400, 6, 0, 10));
            movingObjects.add(new RedPoint(context, 600, 400, -5, 0, 30));
            movingObjects.add(new RedPoint(context, 10, 40, 2, 3, 20));
            movingObjects.add(new RedPoint(context, 580, 230, 1, 5, 10));
            movingObjects.add(new RedPoint(context, 234, 22, 2, 2, 13));
            movingObjects.add(new RedPoint(context, 124, 65, -2, -3, 14));
            movingObjects.add(new RedPoint(context, 333, 233, -2, 4, 25));
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
                    synchronized (mSurfaceHolder) {
                        draw(c);
                    }
                } finally {
                    if (c != null) mSurfaceHolder.unlockCanvasAndPost(c);
                }

            }
            super.run();
        }

        private boolean hasCollision(Movable m1, Movable m2) {
            int distance = Vec2D.distance(m1.getNextPos(), m2.getNextPos()) - m1.getRadius() - m2.getRadius();
            return distance <= 0;
        }

        private Vec2D getContactVector(Movable m1, Movable m2) {
            Vec2D contact = m1.getPosition().subtract(m2.getPosition());
            contact.normalize(1);
            return contact;
        }

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
                    Collision collision = new Collision();
                    collision.setData(m1, m2, contactVector);
                    m1.setVelocity(collision.newV1);
                    m2.setVelocity(collision.newV2);

                }
                m1.draw(canvas);
            }
        }
    }
}
//                for (Movable mm : movingObjects) {
//                    if (m == mm) {
//                        continue;
//                    }
//                    int m_r = m.getRadius();
//                    int m_s = m.getX(); // start
//                    int m_e = m_s + m_r; // end
//                    int m_t = m.getY(); // top
//                    int m_b = m_t + m_r; // bottom
//                    int m_cx = m_s + (m_r / 2); // center x
//                    int m_cy = m_t + (m_r / 2); // center y
//                    int m_vx = m.getVelX();
//                    int m_vy = m.getVelY();
//                    int mm_r = mm.getRadius();
//                    int mm_s = mm.getX();
//                    int mm_e = mm_s + mm_r;
//                    int mm_t = mm.getY();
//                    int mm_b = mm_t + mm_r;
//                    int mm_cx = mm_s + (mm_r / 2); // center x
//                    int mm_cy = mm_t + (mm_r / 2); // center y
//                    int mm_vx = mm.getVelX();
//                    int mm_vy = mm.getVelY();
//                    int c = 5;
//                    boolean linex = (Math.abs(m_cx - mm_cx) <= 20);
//                    boolean liney = (Math.abs(m_cy - mm_cy) <= 20);
//                    boolean hitx = (Math.abs(m_s - mm_e) <= c) || (Math.abs(m_e - mm_s) <= c);
//                    boolean hity = (Math.abs(m_t - mm_b) <= c) || (Math.abs(m_b - mm_t) <= c);
//                    if (hitx && liney) {
//                        m.setVelX(-mm_vx);
//                        mm.setVelX(-m_vx);
//                    }
//                    if (hity && linex) {
//                        m.setVelY(mm_vy);
//                        mm.setVelY(m_vy);
//                    }
