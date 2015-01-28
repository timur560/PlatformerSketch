package org.timur560.platformer.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.main.Game;

import java.util.List;

/**
 * Created by timur on 27.01.15.
 */
public class ActionTerminal extends GameObject {
    public static int STATE_CLOSED = 0;
    public static int STATE_OPENED = 1;

    private int state = STATE_CLOSED;

    public ActionTerminal(Game g, List<Long> pos) {
        super(g);
        shape = new Rectangle(pos.get(0), pos.get(1), 50, 50);
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        if (state == STATE_OPENED) g.setColor(Color.green);
        if (state == STATE_CLOSED) g.setColor(Color.red);
        g.draw(shape);
    }

    public void toggle() {
        if (state == STATE_CLOSED) state = STATE_OPENED;
        else state = STATE_CLOSED;
    }

    public int getState() {
        return state;
    }
}