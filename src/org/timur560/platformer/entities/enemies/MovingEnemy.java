package org.timur560.platformer.entities.enemies;

import org.newdawn.slick.*;
import org.timur560.platformer.Platformer;
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

    public MovingEnemy(Game g, List<Long> vertices, List<List<Long>> path, Double speed, boolean canDie) {
        super(g, vertices);
        this.path = path;
        this.speed = speed.floatValue();
        this.canDie = canDie;
    }

    @Override
    public void init() throws SlickException {
        try {
            staticSprite = new SpriteSheet(new Image(this.getClass().getResource("/res/images/static.png").getFile()), 50,50);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        super.update(gc, delta);
        if (dead || path.isEmpty()) return;

        List<Long> currentPos, prevPos;

        prevPos = path.get(posIndex);
        if (posIndex + 1 >= path.size()) currentPos = path.get(0);
        else currentPos = path.get(posIndex + 1);

        t += speed / delta;

        shape.setX((1 - t) * prevPos.get(0) + t * currentPos.get(0));
        shape.setY((1 - t) * prevPos.get(1) + t * currentPos.get(1));

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
        if (dead) return;

        g.setColor(Color.red);
        if (Platformer.DEBUG_MODE) g.draw(shape);

        g.drawImage(staticSprite.getSubImage(0,1), shape.getX(), shape.getY());
    }

}
