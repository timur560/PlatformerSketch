package org.timur560.platformer.world;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.timur560.platformer.Platformer;
import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.core.Helper;
import org.timur560.platformer.main.Game;

import java.util.List;

/**
 * Created by qwer on 29.01.15.
 */
public class MovingPlatform extends GameObject {
    protected List<List<Long>> path;
    protected Long width;
    protected int posIndex = 0;
    protected float t = 0, speed;

    public MovingPlatform(Game g, List<List<Long>> p, Long w, Double s) {
        super(g);
        path = p;
        width = w;
        speed = s.floatValue();

        float[] pos = Helper.cellsToPx(path.get(0).get(0), path.get(0).get(1) - 1);

        shape = new Rectangle(pos[0], pos[1] + Helper.CELL_SIZE - 20, width * Helper.CELL_SIZE, 20);
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        List<Long> currentPos, prevPos;

        prevPos = path.get(posIndex);
        if (posIndex + 1 >= path.size()) currentPos = path.get(0);
        else currentPos = path.get(posIndex + 1);

        float prevX = shape.getX();
        boolean movePlayer = false;
        Shape ps = game.getPlayer().getShape();

        if (ps.getY() + ps.getHeight() == shape.getY() && ps.getX() + ps.getWidth() > shape.getX()
                && ps.getX() < shape.getX() + shape.getWidth()) {
            movePlayer = true;
        }
        float[] cp = Helper.cellsToPx(currentPos.get(0), currentPos.get(1) - 1);
        float[] pp = Helper.cellsToPx(prevPos.get(0), prevPos.get(1) - 1);

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

        // update player X coordinate
        if (movePlayer) {
            ps.setX(ps.getX() + (shape.getX() - prevX));
        }
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        if (Platformer.DEBUG_MODE) g.draw(shape);

        int i;
        for (i = 0; i < width; i++) {
            g.drawImage(game.getTileset(game.getLevel().getZone().getTileset()).getSubImage(2, 3), shape.getX() + Helper.CELL_SIZE * i, shape.getY() - 30);
        }
    }

    public float getSpeed() {
        return speed;
    }
}
