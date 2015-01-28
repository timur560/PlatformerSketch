package org.timur560.platformer.entities;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.timur560.platformer.core.GameObject;
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
        wallShape = new Rectangle(wall.get(0) + 15, wall.get(1), 20, wall.get(2));
        doorShape = new Rectangle(door.get(0), door.get(1), 50, 100);
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
            g.draw(wallShape);
        }

        g.setColor(Color.black);
        g.fill(doorShape);

        terminal.render(gc, g);
    }

    public boolean intersects(Shape s) {
        return terminal.getState() == ActionTerminal.STATE_CLOSED && s.intersects(wallShape);
    }
}
