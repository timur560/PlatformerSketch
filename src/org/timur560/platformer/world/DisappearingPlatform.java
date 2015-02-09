package org.timur560.platformer.world;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.timur560.platformer.Platformer;
import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.core.Helper;
import org.timur560.platformer.main.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timur on 09.02.15.
 */
public class DisappearingPlatform extends GameObject {
    public static final int DISAPPEAR_DELAY = 400;
    public static final int APPEAR_DELAY = 1000;

    protected List<Long> pos;
    protected Long width;
    protected float speed = 0.2f;
    protected boolean state = true, alreadyOnPlatform = false;
    protected long disappearStartedAt = 0, appearStartedAt = 0;

    public DisappearingPlatform(Game g, Long x, Long y, Long w) {
        super(g);

        pos = new ArrayList<>();
        pos.add(x);
        pos.add(y);
        width = w;

        float[] fpos = Helper.cellsToPx(pos.get(0), pos.get(1) - 1);

        shape = new Rectangle(fpos[0], fpos[1], Helper.CELL_SIZE * width, Helper.CELL_SIZE);
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        if (playerOnPlatform()) {
            if (!alreadyOnPlatform) {
                disappearStartedAt = System.currentTimeMillis();
                alreadyOnPlatform = true;
            }
            if (state && System.currentTimeMillis() - disappearStartedAt >= DISAPPEAR_DELAY) {
                state = false;
            }
        } else {
            if (alreadyOnPlatform) {
                alreadyOnPlatform = false;
                appearStartedAt = System.currentTimeMillis();
            }
            if (!state && System.currentTimeMillis() - appearStartedAt >= APPEAR_DELAY) {
                state = true;
            }
        }
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        if (state) {
            g.setColor(Color.green);
            if (Platformer.DEBUG_MODE) g.draw(shape);

            Image img = game.getTileset(game.getLevel().getZone().getTileset()).getSubImage(1, 0);

            int i;
            for (i = 0; i < width; i++) {
                g.setColor(new Color(1f,1f,1f,0.5f));
                if (playerOnPlatform()) {
                    img.setAlpha(1 - (float)(System.currentTimeMillis() - disappearStartedAt) / (float)DISAPPEAR_DELAY);
                } else {
                    img.setAlpha(1);
                }
                g.drawImage(
                        img,
                        shape.getX() + Helper.CELL_SIZE * i,
                        shape.getY());
            }
        }
    }

    public boolean playerOnPlatform() {
        Shape ps = game.getPlayer().getShape();
        return (ps.getX() + ps.getWidth() > shape.getX()
                && ps.getX() < shape.getX() + shape.getWidth()
                && ps.getY() + ps.getHeight() > shape.getY() - 1
                && ps.getY() + ps.getHeight() < shape.getY());

    }

    public boolean collidesWith(Shape s) {
        return (state && s.intersects(shape));
    }
}
