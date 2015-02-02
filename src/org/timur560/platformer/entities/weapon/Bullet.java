package org.timur560.platformer.entities.weapon;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.timur560.platformer.Platformer;
import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.entities.Player;
import org.timur560.platformer.entities.enemies.Enemy;
import org.timur560.platformer.main.Game;

/**
 * Created by timur on 25.01.15.
 */
public class Bullet extends GameObject {
    protected float x1, y1, x2, y2;
    protected float t = 0, speed = 0.3f;
    protected boolean dead = false;
    protected Weapon weapon;

    public Bullet(Game g, Weapon w, float x1, float y1, float x2 ,float y2) {
        super(g);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        weapon = w;
        shape = new Rectangle(x1, y1, 30, 30);
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        // check if bullet intersects wall
        // TODO

        float speed = this.speed;

        if (weapon.getOwner() instanceof Enemy) {
            speed /= 3;
        }

        t += speed / delta;

        shape.setX((1 - t) * x1 + t * x2);
        shape.setY((1 - t) * y1 + t * y2);

        if (t > 1) {
            die();
        }
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        if (Platformer.DEBUG_MODE) g.draw(shape);
        if (weapon.getOwner() instanceof Player) {
            g.drawImage(game.getTileset(game.getLevel().getZone().getTileset()).getSubImage(2, 1), shape.getX(), shape.getY());
        } else {
            g.drawImage(game.getTileset(game.getLevel().getZone().getTileset()).getSubImage(3, 1), shape.getX(), shape.getY());
        }
    }

    public void die() {
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }
}
