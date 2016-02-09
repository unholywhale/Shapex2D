package com.whale.shapex2d;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.whale.shapex2d.entities.GreenStone;
import com.whale.shapex2d.entities.RedPoint;
import com.whale.shapex2d.geom.Collision;
import com.whale.shapex2d.geom.Vec2D;
import com.whale.shapex2d.interfaces.Movable;
import com.whale.shapex2d.interfaces.Stationary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Drawing view
 */
public class DrawView extends View {

    private ArrayList<Movable> movingObjects = new ArrayList<>();
    private ArrayList<Stationary> stationaryObjects = new ArrayList<>();
    private ArrayList<Stationary> growingObjects = new ArrayList<>();
    public static int FIELD_SIZE = 1000;
    public static int FIELD_ROWS = 100;
    private ArrayList<Vec2D> fieldCells = new ArrayList<>(FIELD_SIZE);
    private HashSet<Vec2D> mCompleted = new HashSet<>();
    private Context mContext;
    private int mPercCompleted = 0;
    private boolean mStop = true;

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
        mContext = context;
    }

    private void clearField() {
        mCompleted.clear();
        fieldCells.clear();
        movingObjects.clear();
        stationaryObjects.clear();
    }

    public void start() {
        //addRedPoint(new Vec2D(500, 500), new Vec2D(0, 0), Integer.MAX_VALUE);
        //addRedPoint(new Vec2D(300, 520), new Vec2D(2, 0), 5);
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
        for (int i = 0; i < 10; i++) {
            addRedPoint(Utils.randomPosition(getWidth(), getHeight()), Utils.randomVelocity(5, 5), 30);
        }
        postInvalidate();
    }

    public void actionDown(Vec2D position) {
        if (mStop) {
            start();
        }
        GreenStone stone = addGreenStone(position);
        growingObjects.add(stone);
    }

    public void actionUp() {
        GreenStone stone = (GreenStone) growingObjects.get(growingObjects.size() - 1);
        stone.setGrowing(false);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////


    public RedPoint addRedPoint(Vec2D position) {
        Vec2D velocity = Utils.randomVelocity(RedPoint.SPEED, RedPoint.SPEED);
        RedPoint redPoint = new RedPoint(mContext, position, velocity);
        movingObjects.add(redPoint);
        return redPoint;
    }

    public RedPoint addRedPoint(Vec2D position, Vec2D velocity) {
        RedPoint redPoint = new RedPoint(mContext, position, velocity);
        movingObjects.add(redPoint);
        return redPoint;
    }

    public RedPoint addRedPoint(Vec2D position, Vec2D velocity, int mass) {
        RedPoint redPoint = new RedPoint(mContext, position, velocity, mass);
        movingObjects.add(redPoint);
        return redPoint;
    }

    public GreenStone addGreenStone(Vec2D position) {
        GreenStone greenStone = new GreenStone(mContext, position);
        greenStone.setGrowing(true);
        stationaryObjects.add(greenStone);
        return greenStone;
    }

    private boolean hasCollision(Movable m1, Movable m2) {
        double distance = Vec2D.distance(m1.getNextPos(), m2.getNextPos()) - m1.getRadius() - m2.getRadius();
        return distance <= 0;
    }

    private boolean hasCollision(Movable m1, Stationary s1) {
        double distance = Vec2D.distance(m1.getNextPos(), s1.getPosition()) - m1.getRadius() - s1.getRadius();
        return distance <= 0;
    }

    private Vec2D getContactVector(Movable m1, Movable m2) {
        Vec2D contact = m1.getPosition().subtract(m2.getPosition());
        contact.normalize(1);
        return contact;
    }

    private Vec2D getContactVector(Movable m1, Stationary s1) {
        Vec2D contact = m1.getPosition().subtract(s1.getPosition());
        contact.normalize(1);
        return contact;
    }

    private Collision collision = new Collision();
    private boolean first = true;
    private double prevTime;
    private double step = 50;
    private Paint textPaint = new Paint();
    @Override
    protected void onDraw(Canvas canvas) {
//        if (first) {
//            prevTime = System.currentTimeMillis() - step;
//            first = false;
//        }
//        while (true) {
//            if (prevTime > System.currentTimeMillis() - step) {
//                break;
//            }
//        }
//        prevTime = System.currentTimeMillis();
        canvas.drawColor(Color.WHITE);
        Movable m1;
        Movable m2;
        Stationary s1;
        double energy = 0;
        double length = 0;
        for (int i = 0; i < movingObjects.size(); i++) {
            m1 = movingObjects.get(i);
            length = m1.getVelocity().getLength();
            energy += (m1.getMass() * length*length) / 2;
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
            for (int k = 0; k < stationaryObjects.size(); k++) {
                s1 = stationaryObjects.get(k);
                if (!hasCollision(m1, s1)) {
                    continue;
                }
                if (s1.isGrowing()) {
                    s1.die();
                    mStop = true;
                }
                Vec2D contactVector = getContactVector(m1, s1);
                collision.setData(m1, s1, contactVector);
                m1.setVelocity(collision.newV1);
            }
//                Log.d("POSITION", m1.getPosition().toString());
//                Log.d("VELOCITY", m1.getVelocity().toString());
//                Log.d("NEXT", m1.getNextPos().toString());
            m1.draw(canvas);
        }

            for (Stationary s : stationaryObjects) {
                if (s.isGrowing()) {
                    for (Vec2D v : fieldCells) {
                        if (Vec2D.distance(v, s.getPosition()) <= s.getRadius()) {
                            mCompleted.add(v);
                        }
                    }
                }
                s.draw(canvas);
            }
//                Paint paint = new Paint();
//                paint.setColor(Color.BLUE);
//                paint.setStrokeWidth(2);
//                for (Vec2D m : mCompleted) {
//                    canvas.drawCircle((int) m.x, (int) m.y, 5, paint);
//                }
        textPaint.setTextSize(32);
        canvas.drawText("ENERGY:" + String.valueOf(energy), 50, 50, textPaint);
        try {
            double fSize = fieldCells.size();
            double cSize = mCompleted.size();
            mPercCompleted = (int) (100 / (fSize / cSize));
        } catch (ArithmeticException s) {
            mPercCompleted = 0;
        }
        canvas.drawText("COMPLETED:" + String.valueOf(mPercCompleted), 800, 50, textPaint);
        if (!mStop) {
            postInvalidate();
        }
    }
}
