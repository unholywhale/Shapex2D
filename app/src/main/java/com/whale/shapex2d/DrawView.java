package com.whale.shapex2d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.whale.shapex2d.animations.Animations;
import com.whale.shapex2d.entities.Base;
import com.whale.shapex2d.entities.Tank;
import com.whale.shapex2d.entities.Tower;
import com.whale.shapex2d.entities.RedPoint;
import com.whale.shapex2d.geom.Collision;
import com.whale.shapex2d.geom.Vec2D;
import com.whale.shapex2d.interfaces.Moving;
import com.whale.shapex2d.interfaces.Stationary;
import com.whale.shapex2d.interfaces.Weapon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.ListIterator;

/**
 * Drawing view
 */
public class DrawView extends View implements View.OnTouchListener {

    private ArrayList<Moving> movingObjects = new ArrayList<>();
    private ArrayList<Stationary> stationaryObjects = new ArrayList<>();
    private Base mBase;
    private Stationary mCurrentObject;
    private GestureDetector mGestureDetector;
    private Vec2D mCurrentTouch;
    private Vec2D mPreviousTouch;
    private boolean isDown;
    public static final int FIELD_SIZE = 1000;
    public static final int FIELD_ROWS = 100;
    private int mRemaining = 150;
    private ArrayList<Vec2D> fieldCells = new ArrayList<>(FIELD_SIZE);
    private HashSet<Vec2D> mCompleted = new HashSet<>();
    private Context mContext;
    private int mPercCompleted = 0;
    private boolean mStop = true;
    private double mEnergy = 0;
    private int mBaseHealth;
    private Bitmap bgBitmap;

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
        Bitmap bg = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.terrain_1);
        bgBitmap = Bitmap.createScaledBitmap(bg, getWidth(), getHeight(), false);
        clearField();
        mRemaining = 20;
        mBase = new Base(mContext, new Vec2D(getWidth()/2, getHeight()+1300), 1400);
        stationaryObjects.add(mBase);
        //prepareFieldGrid();
        new AddEnemyTask().execute();
        postInvalidate();
    }

    private class AddEnemyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Vec2D pos = new Vec2D(Utils.randomWithRange(0, getWidth()), -100);
            Vec2D velocity = new Vec2D(0, 1);
            addTank(pos, velocity);
            mRemaining--;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (mRemaining >= 0) {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    Log.e("AddEnemyTask", "interrupted");
                    e.printStackTrace();
                }
                publishProgress();
            }
            return null;
        }
    }

    private void clearField() {
        mCompleted.clear();
        fieldCells.clear();
        movingObjects.clear();
        stationaryObjects.clear();
    }

    private void prepareFieldGrid() {
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
        if (!mStop) {
            if (mCurrentObject != null) {
                mCurrentObject.action(position);
                mCurrentObject = null;
            } else {
                double touchRadius = 50;
                boolean chosen = false;
                for (Stationary s : stationaryObjects) {
                    if (Vec2D.distance(position, s.getPosition()) - touchRadius - s.getRadius() <= 0) {
                        mCurrentObject = s;
                        chosen = true;
                    }
                }
                if (!chosen) {
                    Tower stone = addTower(position);
                    mCurrentObject = stone;
                }
            }
        }
    }

    public void actionUp() {
        if (mCurrentObject != null) {
            mCurrentObject.cancel();
        }
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
        //tower.setGrowing(true);
        Vec2D newPos;
        for (Stationary s : stationaryObjects) {
            if (Vec2D.distance(position, s.getPosition()) - Tower.DEFAULT_RADIUS - s.getRadius() <= 0) {
//                newPos = Vec2D.diff(s.getPosition(), position);
//                newPos.normalize(Tower.DEFAULT_RADIUS + s.getRadius());
//                position = Vec2D.sum(s.getPosition(), newPos);
            }
        }
        Tower tower = new Tower(mContext, position);
        stationaryObjects.add(tower);
        return tower;
    }

    public Tank addTank(Vec2D position, Vec2D velocity) {
        Tank tank = new Tank(mContext, position, velocity);
        movingObjects.add(tank);
        return tank;
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
//        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bgBitmap, 0, 0, null);

        if (mBase.getHealth() <= 0) {
            mStop = true;
        }
        prepareObjects();

        drawStationary(canvas);
        drawMovable(canvas);
        //drawAim(canvas);
        drawInfo(canvas);

        waitFrame();
        mFrameTime = System.currentTimeMillis();
        if (!mStop) {
            postInvalidate();
        }
    }

    private boolean isAtBoundary(Moving m) {
        if (m.getNextPos().y + m.getRadius() >= getHeight()) {
            return true;
        }
        return false;
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
        ListIterator<Moving> iterator = movingObjects.listIterator();
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
        Moving m1;
        Moving m2;
        Stationary s1;
        mEnergy = 0;
        double length;
        for (int i = 0; i < movingObjects.size(); i++) {
            m1 = movingObjects.get(i);
//            length = m1.getVelocity().getLength();
//            mEnergy += (m1.getMass() * length*length) / 2;
            for (int j = i + 1; j < movingObjects.size(); j++) { // handle collisions with movable
                m2 = movingObjects.get(j);
                if (m2.isDead()) {
                    continue;
                }
                if (!Collision.hasCollision(m1, m2)) {
                    continue;
                }
                if (m1 instanceof Weapon || m2 instanceof Weapon) {
                    m1.hit();
                    m2.hit();
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
                    m1.hit();
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
            Moving b = s.shoot();
            if (b != null) {
                movingObjects.add(b);
            }
            s.draw(canvas, mCurrentTouch);
            if (s == mCurrentObject) {
                linePaint.setColor(Color.YELLOW);
                linePaint.setStrokeWidth(10);
                linePaint.setStyle(Paint.Style.STROKE);
                float x = (float) s.getPosition().x;
                float y = (float) s.getPosition().y;
                float r = (float) s.getRadius() + 10;
                canvas.drawCircle(x, y, r, linePaint);
            }
        }
    }

    private void drawInfo(Canvas canvas) {
        textPaint.setTextSize(32);
//        canvas.drawText("ENERGY:" + String.valueOf(mEnergy), 50, 50, textPaint);
//        try {
//            double fSize = fieldCells.size();
//            double cSize = mCompleted.size();
//            mPercCompleted = (int) (100 / (fSize / cSize));
//        } catch (ArithmeticException s) {
//            mPercCompleted = 0;
//        }
        canvas.drawText("HEALTH: " + String.valueOf(mBase.getHealth()), 800, 50, textPaint);
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
