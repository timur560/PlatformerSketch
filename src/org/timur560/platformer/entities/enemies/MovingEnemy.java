package org.timur560.platformer.entities.enemies;

import org.newdawn.slick.*;
import org.timur560.platformer.Platformer;
import org.timur560.platformer.core.Helper;
import org.timur560.platformer.entities.weapon.Weapon;
import org.timur560.platformer.main.Game;

import java.util.List;

/**
 * Created by qwer on 23.01.15.
 */
public class MovingEnemy extends Enemy {
    private List<List<Long>> path;
    private int posIndex = 0;
    private float t = 0, speed = 0.5f;
    private SpriteSheet staticSprite;

    public MovingEnemy(Game g, String t, boolean cd, List<List<Long>> p, List<Double> r, Double s) {
        super(g, p.get(0), r, t, cd, RIGHT);
        path = p;
        speed = s.floatValue();
    }

    @Override
    public void init() throws SlickException {

    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        super.update(gc, delta);
        if (dead || path.isEmpty()) return;

        List<Long> currentPos, prevPos;

        prevPos = path.get(posIndex);
        if (posIndex + 1 >= path.size()) currentPos = path.get(0);
        else currentPos = path.get(posIndex + 1);

        float[] cp = Helper.cellsToPx(currentPos.get(0), currentPos.get(1) - 1);
        float[] pp = Helper.cellsToPx(prevPos.get(0), prevPos.get(1) - 1);

        if (cp[0] < pp[0]) {
            direction = LEFT;
        } else if (cp[0] > pp[0]) {
            direction = RIGHT;
        }

        t += speed / delta;

        shape.setX((1 - t) * pp[0] + t * cp[0]);
        shape.setY((1 - t) * pp[1] + t * cp[1]);

        if (t > 1) {
            t = 0;
            if (posIndex + 1 >= path.size()) {
                posIndex = 0;
            } else {
                posIndex++;
            }
        }
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        super.render(gc, g);
    }

}
