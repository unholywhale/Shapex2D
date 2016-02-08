package com.whale.shapex2d;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.whale.shapex2d.entities.GreenStone;
import com.whale.shapex2d.entities.RedPoint;
import com.whale.shapex2d.geom.Collision;
import com.whale.shapex2d.geom.Vec2D;
import com.whale.shapex2d.interfaces.Movable;
import com.whale.shapex2d.interfaces.Stationary;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Drawing view
 */
public class DrawView extends View {

    private ArrayList<Movable> movingObjects = new ArrayList<>();
    private ArrayList<Stationary> stationaryObjects = new ArrayList<>();
    private ArrayList<Stationary> growingObjects = new ArrayList<>();
    private Context mContext;

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

    public void start() {
        for (int i = 0; i < 10; i++) {
            addRedPoint(Utils.randomPosition(getWidth(), getHeight()));
        }
    }

    public void actionDown(Vec2D position) {
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

    private Vec2D getContactVector(Movable m1, Movable m2) {
        Vec2D contact = m1.getPosition().subtract(m2.getPosition());
        contact.normalize(1);
        return contact;
    }

    private Collision collision = new Collision();

    @Override
    protected void onDraw(Canvas canvas) {
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

            for (Stationary s : stationaryObjects) {
                s.draw(canvas);
            }

        postInvalidate();
    }
}
