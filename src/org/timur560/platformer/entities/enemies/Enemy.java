package org.timur560.platformer.entities.enemies;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.timur560.platformer.Platformer;
import org.timur560.platformer.core.Active;
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
 *          "path" : [[2, 24], [4,24]] // if 1 pair - static cell coordinates, if more - moving between them
 *          "speed" : 0.3, // required for moving
 *          "canDie" : true,
 *          "weapon" : true,
 *          "type" : "snowman | snowflake",
 *          "direction" : 1 | 2 | 3 | 4 // 1 - up, 2 - right, ...
 *       }
 *
 */
public abstract class Enemy extends GameObject implements Active {
    protected boolean dead;
    protected boolean canDie;
    protected Weapon weapon;
    protected long prevShoot = 0;
    protected int shootingDelay = 1000;
    protected String type = "snowflake";
    protected int direction = RIGHT;

    public Enemy(Game g, List<Long> pos, String t, boolean cd, int d) {
        super(g);
        type = t;
        canDie = cd;
        direction = d;
        shape = Helper.cellsToPolygon(pos.get(0), pos.get(1), pos.get(0) + 1, pos.get(1));
    }

    public void setWeapon(Weapon w, int sd) {
        weapon = w;
        shootingDelay = sd;
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        if (dead) return;
        g.setColor(Color.red);
        if (Platformer.DEBUG_MODE) g.draw(shape);
        g.drawImage(getSprite(), shape.getX(), shape.getY());
        if (weapon != null) weapon.render(gc, g);
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        if (weapon != null) {
            if (prevShoot == 0 || System.currentTimeMillis() - shootingDelay > prevShoot) {
                weapon.act();
                prevShoot = System.currentTimeMillis();
            }
            weapon.update(gc, delta);
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

    public int getDirection() {
        return direction;
    }

    protected Image getSprite() {
        if (type.equals("snowflake")) {
            return game.getTileset().getSubImage(0, 1);
        } else if (type.equals("snowman") && direction == LEFT) {
            return game.getTileset().getSubImage(3, 0);
        } else if (type.equals("snowman") && direction == RIGHT) {
            return game.getTileset().getSubImage(4, 0);
        }

        return null;
    }
}
