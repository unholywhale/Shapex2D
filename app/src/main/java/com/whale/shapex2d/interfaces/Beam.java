package com.whale.shapex2d.interfaces;

import com.whale.shapex2d.geom.Vec2D;

/**
 * Created by alex on 23/02/16.
 */
public interface Beam extends Weapon {
    Vec2D getStart();
    Vec2D getEnd();
    double getThickness();
}
