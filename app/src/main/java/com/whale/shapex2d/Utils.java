package com.whale.shapex2d;

import com.whale.shapex2d.geom.Vec2D;

/**
 * Created by alex on 08/02/16.
 */
public class Utils {
    public static int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    public static Vec2D randomPosition(int rangeX, int rangeY) {
        return new Vec2D(randomWithRange(0, rangeX), randomWithRange(0, rangeY));
    }

    public static Vec2D randomVelocity(int rangeX, int rangeY) {
        return new Vec2D(randomWithRange(-rangeX, rangeX), randomWithRange(-rangeY, rangeY));
    }
}
