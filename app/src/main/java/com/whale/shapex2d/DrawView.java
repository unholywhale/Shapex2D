package com.whale.shapex2d;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.whale.shapex2d.animations.Animations;
import com.whale.shapex2d.entities.Tower;
import com.whale.shapex2d.entities.RedPoint;
import com.whale.shapex2d.geom.Collision;
import com.whale.shapex2d.geom.Vec2D;
import com.whale.shapex2d.interfaces.Movable;
import com.whale.shapex2d.interfaces.Stationary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.ListIterator;

/**
 * Drawing view
 */
public class DrawView extends View implements View.OnTouchListener {

    private ArrayList<Movable> movingObjects = new ArrayList<>();
    private ArrayList<Stationary> stationaryObjects = new ArrayList<>();
    private Stationary mCurrentObject;
    private GestureDetector mGestureDetector;
    private Vec2D mCurrentTouch;
    private Vec2D mPreviousTouch;
    private boolean isDown;
    public static int FIELD_SIZE = 1000;
    public static int FIELD_ROWS = 100;
    private ArrayList<Vec2D> fieldCells = new ArrayList<>(FIELD_SIZE);
    private HashSet<Vec2D> mCompleted = new HashSet<>();
    private Context mContext;
    private int mPercCompleted = 0;
    private boolean mStop = true;
    private double mEnergy = 0;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////
    public DrawView(Context context) {
        super(context);
        init(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mGestureDetector = new GestureDetector(mContext, new GestureHandler(this));
        mGestureDetector.setIsLongpressEnabled(false);
        mContext = context;
        Animations.INSTANCE.init(context);
        setOnTouchListener(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // START
    ///////////////////////////////////////////////////////////////////////////
    public void start() {

        mStop = false;
        clearField();
        int cellX = getWidth() / FIELD_ROWS;
        int cellY = getHeight() / FIELD_ROWS;
        int x, y;
        int k = 0;
        for (int i = 0; i < FIELD_ROWS; i++) {
            for (int j = 0; j < FIELD_ROWS; j++) {
                x = cellX + cellX*i - cellX/2;
                y = cellY + cellY*j - cellY/2;
                fieldCells.add(new Vec2D(x, y));
                k++;
            }
        }
        for (int i = 0; i < 30; i++) {
            addRedPoint(Utils.randomPosition(getWidth(), getHeight()), Utils.randomVelocity(5, 5), 1);
        }
        postInvalidate();
    }

    private void clearField() {
        mCompleted.clear();
        fieldCells.clear();
        movingObjects.clear();
        stationaryObjects.clear();
    }

    ///////////////////////////////////////////////////////////////////////////
    // TOUCH HANDLING
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) { // Gesture detector doesn't handle simple up action
            actionUp();
        }
        return mGestureDetector.onTouchEvent(event);

    }

    public void setCurrentTouch(MotionEvent e1, MotionEvent e2) {
        mCurrentTouch = new Vec2D(e2.getX(), e2.getY());
        mPreviousTouch = new Vec2D(e1.getX(), e1.getY());
    }

    public void actionDown(Vec2D position) {
        Tower stone = addTower(position);
        mCurrentObject = stone;
    }

    public void actionUp() {
        mCurrentObject.cancel();
        mCurrentTouch = null;
        mPreviousTouch = null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // ADDING OBJECTS
    ///////////////////////////////////////////////////////////////////////////
    public RedPoint addRedPoint(Vec2D position, Vec2D velocity, int mass) {
        RedPoint redPoint = new RedPoint(mContext, position, velocity, mass);
        movingObjects.add(redPoint);
        return redPoint;
    }

    public Tower addTower(Vec2D position) {
        Tower tower = new Tower(mContext, position);
        //tower.setGrowing(true);
        stationaryObjects.add(tower);
        return tower;
    }



    private Collision collision = new Collision();
    private Paint textPaint = new Paint();
    private Paint linePaint = new Paint();
    private long mFrameTime = 0;
    public static final long FRAME_TIME = 15;
    @Override
    protected void onDraw(Canvas canvas) {
        if (mFrameTime == 0) {
            mFrameTime = System.currentTimeMillis();
        }
        canvas.drawColor(Color.WHITE);

        prepareObjects();

        drawMovable(canvas);
        drawStationary(canvas);
        //drawAim(canvas);
        //drawInfo(canvas);


        waitFrame();
        mFrameTime = System.currentTimeMillis();
        if (!mStop) {
            postInvalidate();
        }
    }

    private void waitFrame() {
        while (System.currentTimeMillis() - FRAME_TIME < mFrameTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("THREAD", "interrupted");
            }
        }
    }

    private void prepareObjects() {
        ListIterator<Movable> iterator = movingObjects.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().isDelete()) {
                iterator.remove();
            }
        }
        ListIterator<Stationary> stIterator = stationaryObjects.listIterator();
        while (stIterator.hasNext()) {
            if (stIterator.next().isDelete()) {
                stIterator.remove();
            }
        }
    }

    private void drawMovable(Canvas canvas) {
        Movable m1;
        Movable m2;
        Stationary s1;
        mEnergy = 0;
        double length;
        for (int i = 0; i < movingObjects.size(); i++) {
            m1 = movingObjects.get(i);
            length = m1.getVelocity().getLength();
            mEnergy += (m1.getMass() * length*length) / 2;
            for (int j = i + 1; j < movingObjects.size(); j++) { // handle collisions with movable
                m2 = movingObjects.get(j);
                if (!Collision.hasCollision(m1, m2)) {
                    continue;
                }
                Vec2D contactVector = Collision.getContactVector(m1, m2);
                collision.setData(m1, m2, contactVector);
                m1.setVelocity(collision.newV1);
                m2.setVelocity(collision.newV2);
            }
            for (int k = 0; k < stationaryObjects.size(); k++) { // handle collisions with stationary
                s1 = stationaryObjects.get(k);
                if (!Collision.hasCollision(m1, s1)) {
                    continue;
                }
//                if (s1.isVulnerable()) {
//                    s1.die();
//                    mStop = true;
//                    isDown = false;
//                }
                if (!m1.isDead()) {
                    s1.hit();
                    m1.die();
                }
                if (!m1.isDead()) {
                    Vec2D contactVector = Collision.getContactVector(m1, s1);
                    collision.setData(m1, contactVector);
                    m1.setVelocity(collision.newV1);
                }
            }
            m1.draw(canvas);
        }
    }

    private void drawStationary(Canvas canvas) {
        for (Stationary s : stationaryObjects) { // draw stationary
            s.draw(canvas, mCurrentTouch);
        }
    }

    private void drawInfo(Canvas canvas) {
        textPaint.setTextSize(32);
        canvas.drawText("ENERGY:" + String.valueOf(mEnergy), 50, 50, textPaint);
        try {
            double fSize = fieldCells.size();
            double cSize = mCompleted.size();
            mPercCompleted = (int) (100 / (fSize / cSize));
        } catch (ArithmeticException s) {
            mPercCompleted = 0;
        }
        canvas.drawText("COMPLETED:" + String.valueOf(mPercCompleted), 800, 50, textPaint);
    }

    private void drawAim(Canvas canvas) {
        if (mCurrentTouch != null) {
            linePaint.setColor(Color.BLUE);
            linePaint.setStrokeWidth(2);
            linePaint.setStyle(Paint.Style.STROKE);
            Vec2D aim = Vec2D.diff(mCurrentTouch, mPreviousTouch);
            double c = 1;
            double incX = aim.x > 0 ? 1 : -1;
            double incY = aim.y > 0 ? 1 : -1;
            double x = mCurrentTouch.x;
            double y = mCurrentTouch.y;
            if (aim.x != 0) {
                c = Math.abs(aim.y / aim.x);
            }
            boolean result = x > 0 && x < getWidth() && y > 0 && y < getHeight();
            while (result) {
                x = x - incX;
                y = y - incY*c;
                result = x > 0 && x < getWidth() && y > 0 && y < getHeight();
            }
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            if (x > getWidth()) x = getWidth();
            if (y > getHeight()) y = getHeight();

            canvas.drawLine((float) mPreviousTouch.x, (float) mPreviousTouch.y, (float) x, (float) y, linePaint);
        }
    }



}
