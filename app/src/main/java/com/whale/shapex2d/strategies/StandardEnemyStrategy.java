package com.whale.shapex2d.strategies;

import com.whale.shapex2d.geom.Vec2D;
import com.whale.shapex2d.interfaces.Enemy;
import com.whale.shapex2d.interfaces.Entity;
import com.whale.shapex2d.interfaces.Projectile;

import java.util.ArrayList;

/**
 * Standard enemy targeting strategy
 */
public class StandardEnemyStrategy {

    public static Entity getTarget(ArrayList<Entity> objects, Entity self) {
        Entity target = null;
        double distance = -1;
        double newDistance;
        for (Entity e : objects) {
            if (e == self || e instanceof Enemy || e instanceof Projectile) { // don't target enemies or projectiles
                continue;
            }
            newDistance = Vec2D.distance(self.getPosition(), e.getPosition());
            if (newDistance < distance || distance == -1) {
                distance = newDistance;
                target = e;
            }
        }
        return target;
    }
}
