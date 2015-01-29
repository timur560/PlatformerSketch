package org.timur560.platformer.entities.weapon;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.timur560.platformer.Platformer;
import org.timur560.platformer.core.Active;
import org.timur560.platformer.entities.Player;
import org.timur560.platformer.entities.enemies.*;
import org.timur560.platformer.main.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qwer on 23.01.15.
 */
public class Gun extends Weapon implements Shootable {
    protected List<Bullet> bullets;
    protected int maxBulletsCount = 100;

    public Gun(Game g, Active o) {
        super(g, o);
//        shape = new Rectangle(owner.getShape().getX() + 20, owner.getShape().getY() - 20, 30, 15);
        bullets = new ArrayList<>();
    }

    // shoot
    public void act() {
        if (prevAct == 0) {
            prevAct = System.currentTimeMillis();
        } else if (prevAct + delay > System.currentTimeMillis()) {
            return;
        }

        prevAct = System.currentTimeMillis();

        Shape s = owner.getShape();


        if (bullets.size() < maxBulletsCount) {
            float destX = s.getX(), destY = s.getY();
            if (owner.getDirection() == Player.UP) {
                destY -= 500;
            } else if (owner.getDirection() == Player.RIGHT) {
                destX += 500;
            } else if (owner.getDirection() == Player.DOWN) {
                destY += 500;
            } else if (owner.getDirection() == Player.LEFT) {
                destX -= 500;
            }

            bullets.add(new Bullet(game, this, s.getX() + 15, s.getY() + 15, destX + 20, destY + 20));
        }
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        // check bullets if can kill enemy
        if (owner instanceof Player) {
            enemiesLoop:
            for (Enemy e : game.getLevel().getEnemies()) {
                for (Bullet b : bullets) {
                    if (b.getShape().intersects(e.getShape())) {
                        e.die();
                        b.die();
                        continue enemiesLoop;
                    }
                }
            }
        } else {
            for (Bullet b : bullets) {
                if (b.getShape().intersects(game.getPlayer().getShape())) {
                    game.getPlayer().die();
                    b.die();
                    break;
                }
            }
        }

        // update bullet position
//        for (Bullet b : bullets) {
//            b.update(gc, delta);
//            if (b.isDead()) {
//                bullets.remove(b);
//            }
//        }

        int i = 0;
        for (i = 0; i < bullets.size(); i++) {
            bullets.get(i).update(gc, delta);
            if (bullets.get(i).isDead()) {
                bullets.remove(i);
            }
        }
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        for (Bullet b : bullets) {
            b.render(gc, g);
        }
    }

}
