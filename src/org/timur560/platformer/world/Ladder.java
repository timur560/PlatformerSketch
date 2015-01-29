package org.timur560.platformer.world;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.timur560.platformer.Platformer;
import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.core.Helper;
import org.timur560.platformer.main.Game;

/**
 * Created by timur on 22.01.15.
 */
public class Ladder extends GameObject {
    public Ladder(Game g, long x1, long x2, long length) {
        super(g);
        float[] c = Helper.cellsToPx(x1, x2);
        shape = new Rectangle(c[0] + 15, c[1], 20, length * Helper.CELL_SIZE);
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {

    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.setColor(Color.yellow);
        if (Platformer.DEBUG_MODE) g.draw(shape);
//            int i;
//            for (i = 0; i <= l.toShape().getHeight(); i += 50) {
//                g.drawImage(staticSprite.getSubImage(0,0), l.toShape().getX() - 10, l.toShape().getY() + i);
//            }
    }
}
