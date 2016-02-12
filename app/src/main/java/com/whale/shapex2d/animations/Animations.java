package com.whale.shapex2d.animations;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.whale.shapex2d.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

public enum Animations {
    INSTANCE;
    public static final String ANIM_EXPLOSION = "explosion_";
    public static final String ANIM_CIRCLE_EXPLOSION = "circleexplosion_";

    private Context mContext;
    private HashSet<ArrayList<Drawable>> mAnimations = new HashSet<>();
    private ArrayList<Drawable> mAnimation;

    public void init(Context context) {
        mContext = context;
    }

    public ArrayList<Drawable> getAnimation(String type) throws Resources.NotFoundException {
        Resources resources = mContext.getResources();
        ArrayList<Drawable> animation = new ArrayList<>();
        int i = 0;
        Drawable d;
        int id = resources.getIdentifier(type + String.valueOf(i), "drawable", mContext.getPackageName());
        d = mContext.getResources().getDrawable(id);
        while (d != null) {
            animation.add(d);
            i++;
            id = resources.getIdentifier(type + String.valueOf(i), "drawable", mContext.getPackageName());
            try {
                d = mContext.getResources().getDrawable(id);
            } catch (Resources.NotFoundException e) {
                d = null;
            }
        }
        if (animation.size() == 0) {
            throw new Resources.NotFoundException();
        }
        return animation;
    }
}
