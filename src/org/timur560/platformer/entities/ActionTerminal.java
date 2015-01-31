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
public class ActionTerminal extends GameObject {
    public static int STATE_INACTIVE = 0;
    public static int STATE_CLOSED = 1;
    public static int STATE_OPENED = 2;

    private int state = STATE_INACTIVE;

    public ActionTerminal(Game g, List<Long> pos) {
        super(g);
        float[] c = Helper.cellsToPx(pos.get(0), pos.get(1));
        shape = new Rectangle(c[0], c[1], Helper.CELL_SIZE, Helper.CELL_SIZE);
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        if (game.getLevel().getHeartsCollected() == game.getLevel().getHeartsTotal()
                && state == STATE_INACTIVE) {
            state = STATE_CLOSED;
        }
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        if (state == STATE_INACTIVE) {
            g.setColor(Color.black);
            g.drawImage(game.getTileset().getSubImage(3, 2), shape.getX(), shape.getY());
        } else if (state == STATE_OPENED) {
            g.setColor(Color.green);
            g.drawImage(game.getTileset().getSubImage(1, 2), shape.getX(), shape.getY());
        } else if (state == STATE_CLOSED) {
            g.setColor(Color.red);
            g.drawImage(game.getTileset().getSubImage(2, 2), shape.getX(), shape.getY());
        }
        if (Platformer.DEBUG_MODE) g.draw(shape);
    }

    public void toggle() {
        if (state == STATE_INACTIVE) return;
        if (state == STATE_CLOSED) state = STATE_OPENED;
        else state = STATE_CLOSED;
    }

    public int getState() {
        return state;
    }
}
