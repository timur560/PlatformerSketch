package org.timur560.platformer.world;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;

/**
 * Created by timur on 22.01.15.
 */
public class Ladder {
    private Shape shape;

    public Ladder(float[] vertices) {
        shape = new Polygon(vertices);
    }

//    public void init(GameContainer gc) throws SlickException {
//
//    }

    public boolean collidesWith (Shape s) {
        if (shape.intersects(s) || shape.contains(s)) return true;

        return false;
    }

    public Shape toShape() {
        return shape;
    }

}
