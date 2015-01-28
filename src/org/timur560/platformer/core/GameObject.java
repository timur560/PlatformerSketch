package org.timur560.platformer.core;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Shape;
import org.timur560.platformer.main.Game;

/**
 * Created by timur560 on 28.01.15.
 */
public abstract class GameObject {
    protected Shape shape;
    protected Game game;
    protected boolean initialized = false;

    public abstract void update(GameContainer gc, int delta) throws SlickException;
    public abstract void render(GameContainer gc, Graphics g) throws SlickException;

    public GameObject(Game g) {
        game = g;
    }

    public void init() throws SlickException {
        initialized = true;
    }

    public final boolean getInitialized() {
        return initialized;
    }

    public final Shape getShape() {
        return shape;
    }

}
