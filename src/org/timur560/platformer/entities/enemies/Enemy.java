package org.timur560.platformer.entities.enemies;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.core.Helper;
import org.timur560.platformer.entities.weapon.Weapon;
import org.timur560.platformer.main.Game;

import java.util.List;

/**
 * Created by qwer on 23.01.15.
 *
 *   "enemies" : [
 *       {
 *          "rect" : [4, 0.5, 0, 0],  /// width (4 * Platformer.CELL_SIZE), height, left offset, top offset
 *          "path" : [2, 24, 4,24] // if 2 numbers - static cell coordinates, if 4 - moving between them
 *          "speed" : 0.3, // required for moving
 *          "canDie" : true,
 *          "weapon" : true
 *       }
 *
 */
public abstract class Enemy extends GameObject {
    protected boolean dead;
    protected boolean canDie;
    protected Weapon weapon;
    protected long prevShoot = 0;
    protected int shootingDelay = 1000;

    public Enemy(Game g, List<Long> vertices) {
        super(g);
        shape = Helper.cellsToPolygon(vertices.get(0), vertices.get(1), vertices.get(2), vertices.get(3));
    }

    public Enemy(Game g, List<Long> vertices, Weapon w) {
        this(g, vertices);
        weapon = w;
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.setColor(Color.red);
        if (!dead) g.draw(shape);
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        if (weapon != null) {
            if (prevShoot == 0 || prevShoot + shootingDelay > System.currentTimeMillis()) {
                weapon.act();
                prevShoot = System.currentTimeMillis();
            }
        }
    }

    public void die() {
        if (!canDie) return;
        dead = true;
        shape = null;
    }

    public boolean isDead() {
        return dead;
    }
}
