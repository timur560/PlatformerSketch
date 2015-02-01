package org.timur560.platformer.entities;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.timur560.platformer.Platformer;
import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.core.Helper;
import org.timur560.platformer.main.Game;

/**
 * Created by qwer on 29.01.15.
 */
public class Heart extends GameObject {
    protected boolean collected = false;
    protected boolean opened = false;
    protected String secret = "none"; // none | snow | box
    protected boolean fake;
    protected Shape secretShape;

    protected boolean animate = false;
    protected int animationSteps = 0;


    public Heart(Game g, Long x, Long y, String s, Boolean f) {
        super(g);
        secret = s;
        fake = f;
        float[] c = Helper.cellsToPx(x, y);
        shape = new Rectangle(c[0] + 15, c[1] + 15 - Helper.CELL_SIZE, 20, 20);

        if (secret.equals("none")) {
            opened = true;
        } else if (secret.equals("box")) {
            secretShape = new Rectangle(c[0] + 10, c[1] + 10, 30, 40);
        } else if (secret.equals("snowheap")) {
            secretShape = new Rectangle(c[0] + 10, c[1] + 30, 40, 20);
        }
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {

        if (animate) {
            shape.setY(shape.getY() - 2);
            secretShape.setY(secretShape.getY() + 1);
            // shape = shape.transform(Transform.createRotateTransform(1f)); // not works :(((
            animationSteps++;
            if (animationSteps > 50 / 2) {
                animate = false;
                animationSteps = 0;
            }
            return;
        }

        if (!opened) {
            if (gc.getInput().isKeyDown(Input.KEY_C)) { // open secret
                if (secretShape != null && secretShape.intersects(game.getPlayer().getShape())) {
                    opened = true;
                    shape.setY(shape.getY() + 50);
                    animate = true;
                }
            }
        } else {
            if (game.getPlayer().getShape().intersects(shape) && !collected) {
                collected = true;
            }
        }
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        if (collected) return;

        if (!opened && secretShape != null || animate) {
            g.setColor(Color.blue);

            if (Platformer.DEBUG_MODE) g.draw(secretShape);

            if (secret.equals("box")) {
                g.drawImage(game.getTileset().getSubImage(1, 4), secretShape.getX() - 10, secretShape.getY() - 10);
            } else if (secret.equals("snowheap")) {
                g.drawImage(game.getTileset().getSubImage(2, 0), secretShape.getX() - 10, secretShape.getY() - 30);
            }
        }

        if (!fake && opened) {
            g.setColor(Color.red);
            if (Platformer.DEBUG_MODE) g.draw(shape);
            g.drawImage(game.getTileset().getSubImage(0, 4), shape.getX(), shape.getY());
        }
    }

    public boolean getFake() {
        return fake;
    }

    public boolean getCollected() {
        return collected;
    }
}
