package com.whale.shapex2d;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.whale.shapex2d.geom.Vec2D;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private DrawView mDrawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawView = (DrawView) findViewById(R.id.draw_view);
        ViewTreeObserver vto = mDrawView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mDrawView.start();
                mDrawView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        //mDrawView.setOnTouchListener(this);
//        mSurfaceView = (ShapexView) findViewById(R.id.surface_view);
//        mSurfaceView.setOnTouchListener(this);
//        mSurfaceView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDrawView.actionDown(new Vec2D(event.getX(), event.getY()));
                return true;
            case MotionEvent.ACTION_UP:
                mDrawView.actionUp();
                return true;
            default:
                break;
        }
        return false;
    }
}
