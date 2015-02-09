package org.timur560.platformer.world;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.timur560.platformer.Platformer;
import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.core.Helper;
import org.timur560.platformer.main.Game;

/**
 * Created by timur on 31.01.15.
 */
public class MovingBlock extends GameObject {
    private float vY = 0, gravity = 0.7f;
    private int iterations = 5;
    private float[] initialPos;

    public MovingBlock(Game g, Long x, Long y) {
        super(g);
        float[] pos = Helper.cellsToPx(x, y);
        shape = new Rectangle(pos[0] + 5, pos[1] + 10, Helper.CELL_SIZE - 10, Helper.CELL_SIZE - 10);
        initialPos = new float[]{pos[0] + 5, pos[1] + 10};
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        // Y acceleration
        vY += gravity;

        // Y collisions
        float vYtemp = vY / iterations;

        for (int i = 0; i < iterations; i++) {
            shape.setY(shape.getY() + vYtemp);

            MovingPlatform mp = game.getLevel().collidesWithMovingPlatform(shape);

            if (game.getLevel().collidesWith(shape) || mp != null) {
                shape.setY(shape.getY() - vYtemp);

                if (mp != null) {
                    if (shape.getY() < mp.getShape().getY()) shape.setY(mp.getShape().getY() - shape.getHeight());
                    else shape.setY(mp.getShape().getY() + mp.getShape().getHeight() + 2);
                }

                vY = 0;
                break;
            }
        }

        // player collision
        Shape ps = game.getPlayer().getShape();
        if (shape.intersects(game.getPlayer().getShape())) {
            // if (shape.getX() > 0) {
                if (shape.getX() < ps.getX() + ps.getWidth() && shape.getX() > ps.getX()) {
                    shape.setX(ps.getX() + ps.getWidth() + 1);
                } else if (shape.getX() + shape.getWidth() < ps.getX() + ps.getWidth() && shape.getX() + shape.getWidth() > ps.getX()) {
                    shape.setX(ps.getX() - shape.getWidth() - 1);
                }
            // }
        }

        if (shape.getY() > game.getLevel().getHeight()
                || shape.getX() + shape.getWidth() < 0
                || shape.getX() > game.getLevel().getWidth()) {
            shape.setX(initialPos[0]);
            shape.setY(initialPos[1]);
        }
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.setColor(Color.blue);
        if (Platformer.DEBUG_MODE) g.draw(shape);
        g.drawImage(game.getTileset(game.getLevel().getZone().getTileset()).getSubImage(0, 0), shape.getX() - 5, shape.getY() - 10);
    }
}
