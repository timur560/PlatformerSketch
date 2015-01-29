package org.timur560.platformer.core;

import org.newdawn.slick.geom.Shape;

/**
 * Created by timur on 29.01.15.
 */
public interface Active {
    public static int UP    = 1;
    public static int RIGHT = 2;
    public static int DOWN  = 3;
    public static int LEFT  = 4;

    public Shape getShape();
    public int getDirection();
}
