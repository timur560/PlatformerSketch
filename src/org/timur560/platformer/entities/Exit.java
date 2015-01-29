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
 */
public class Exit extends GameObject {
    private ActionTerminal terminal;
    private Shape wallShape, doorShape;
    private String nextLevel;
    private long prevToggle = 0;
    private int delay = 300;

    public Exit(Game g, List<Long> wall, List<Long> door, ActionTerminal at) {
        super(g);
        terminal = at;
        float[] cWall = Helper.cellsToPx(wall.get(0), wall.get(1));
        float[] cDoor = Helper.cellsToPx(door.get(0), door.get(1));
        wallShape = new Rectangle(cWall[0] + 15, cWall[1], 20, wall.get(2) * Helper.CELL_SIZE);
        doorShape = new Rectangle(cDoor[0], cDoor[1], Helper.CELL_SIZE, Helper.CELL_SIZE);
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        if (gc.getInput().isKeyDown(Input.KEY_C)) {
            if (terminal.getShape().intersects(game.getPlayer().getShape())
                    && (prevToggle == 0 || System.currentTimeMillis() >= prevToggle + delay)) {
                terminal.toggle();
                prevToggle = System.currentTimeMillis();
            }
        }
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        if (terminal.getState() == ActionTerminal.STATE_CLOSED) {
            if (Platformer.DEBUG_MODE) g.draw(wallShape);
            int i;
            for (i = 0; i < wallShape.getHeight(); i += 50) {
                g.drawImage(game.getTileset().getSubImage(0, 2), wallShape.getX() - 15, wallShape.getY() + i);
            }
        }

        g.setColor(Color.black);
        if (Platformer.DEBUG_MODE) g.draw(doorShape);
        g.drawImage(game.getTileset().getSubImage(2, 4), doorShape.getX(), doorShape.getY());

        terminal.render(gc, g);
    }

    public boolean intersects(Shape s) {
        return terminal.getState() == ActionTerminal.STATE_CLOSED && s.intersects(wallShape);
    }
}
