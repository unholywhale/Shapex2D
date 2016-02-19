package com.whale.shapex2d.interfaces;

import com.whale.shapex2d.geom.Vec2D;

/**
 * Created by alex on 17/02/16.
 */
public interface Entity {
    public boolean isDead();
    public boolean isDelete();
    public void hit();
    public void hit(double dmg);
    Vec2D getNextPos();
    Vec2D getPosition();
    Vec2D getVelocity();
    double getMass();
    double getRadius();
    void setVelocity(Vec2D velocity);
}
