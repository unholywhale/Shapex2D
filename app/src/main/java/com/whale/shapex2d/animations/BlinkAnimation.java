package com.whale.shapex2d.animations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.whale.shapex2d.R;

import java.util.ArrayList;

public enum BlinkAnimation {
    INSTANCE;

    private ArrayList<Drawable> mAnimation;

    public void init(Context context) {
        mAnimation = new ArrayList<>();
        boolean b = false;
        for (int i = 0; i < 15; i++) {
            if (i % 2 == 0) {
                mAnimation.add(context.getResources().getDrawable(R.drawable.point_small));
                mAnimation.add(context.getResources().getDrawable(R.drawable.point_small));
                mAnimation.add(context.getResources().getDrawable(R.drawable.point_small));
            } else {
                mAnimation.add(context.getResources().getDrawable(R.drawable.greenpoint_small));
                mAnimation.add(context.getResources().getDrawable(R.drawable.greenpoint_small));
                mAnimation.add(context.getResources().getDrawable(R.drawable.greenpoint_small));
            }
        }
    }

    public ArrayList<Drawable> getAnimation() {
        return mAnimation;
    }
}
