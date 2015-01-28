package org.timur560.platformer.entities.enemies;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.main.Game;

/**
 * Created by qwer on 23.01.15.
 */
public abstract class Enemy extends GameObject {
    protected boolean dead;
    protected boolean canDie;

    public Enemy(Game g, float[] vertices) {
        super(g);
        shape = new Polygon(vertices);
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        if (!dead) g.draw(shape);
    }

//    public void update(GameContainer gc, int delta) throws SlickException {
//
//    }

    public void die() {
        if (!canDie) return;
        dead = true;
        shape = null;
    }

    public boolean isDead() {
        return dead;
    }
}
