package org.timur560.platformer.world;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.timur560.platformer.core.Helper;

/**
 * Created by timur on 22.01.15.
 */
public class Ladder {
    private Shape shape;

    public Ladder(long x1, long x2, long length) {
        // shape = new Polygon(vertices);

        float[] c = Helper.cellsToPx(x1, x2);

        shape = new Rectangle(c[0] + 15, c[1], 20, length * Helper.CELL_SIZE);
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
