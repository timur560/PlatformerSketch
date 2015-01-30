package org.timur560.platformer.entities;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.timur560.platformer.Platformer;
import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.core.Helper;
import org.timur560.platformer.main.Game;

import java.util.List;

/**
 * Created by timur on 27.01.15.

 {
    "pos": [2, 5], // [x, y]
    "dest": [1, 1, 1] // [level, zone, potral] - if level is not current, start new level; if zone = 0 && portal == 0 - entry point
    "wall": [10, 7, 3], // [x, y, length] - if sub-level != 0 - not required
    "terminal": [9, 13], // [x, y]
 }
 */
public class Portal extends GameObject {
    protected ActionTerminal terminal;
    protected Shape wallShape, portalShape;
    protected long prevToggle = 0;
    protected int delay = 300;
    protected List<Long> dest;

    public Portal(Game g, List<Long> pos, List<Long> d) {
        super(g);
        float[] cPos = Helper.cellsToPx(pos.get(0), pos.get(1));
        portalShape = new Rectangle(cPos[0], cPos[1], Helper.CELL_SIZE, Helper.CELL_SIZE);
        dest = d;
    }

    public Portal(Game g, List<Long> pos, List<Long> d, List<Long> wall, ActionTerminal at) {
        this(g, pos, d);
        terminal = at;
        float[] cWall = Helper.cellsToPx(wall.get(0), wall.get(1));
        wallShape = new Rectangle(cWall[0] + 15, cWall[1], 20, wall.get(2) * Helper.CELL_SIZE);
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        if (gc.getInput().isKeyDown(Input.KEY_C) && terminal != null) {
            if (terminal.getShape().intersects(game.getPlayer().getShape())
                    && (prevToggle == 0 || System.currentTimeMillis() >= prevToggle + delay)) {
                terminal.toggle();
                prevToggle = System.currentTimeMillis();
            }
        }
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        if (terminal != null) {
            if (terminal.getState() == ActionTerminal.STATE_CLOSED) {
                if (Platformer.DEBUG_MODE) g.draw(wallShape);
                int i;
                for (i = 0; i < wallShape.getHeight(); i += 50) {
                    g.drawImage(game.getTileset().getSubImage(0, 2), wallShape.getX() - 15, wallShape.getY() + i);
                }
            }
            terminal.render(gc, g);
        }

        g.setColor(Color.black);
        if (Platformer.DEBUG_MODE) g.draw(portalShape);
        g.drawImage(game.getTileset().getSubImage(2, 4), portalShape.getX(), portalShape.getY());
    }

    public boolean intersects(Shape s) {
        if (terminal != null) {
            return terminal.getState() == ActionTerminal.STATE_CLOSED && s.intersects(wallShape);
        } else {
            return false;
        }
    }
}
