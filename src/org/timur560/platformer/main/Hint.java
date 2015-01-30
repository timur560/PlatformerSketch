package org.timur560.platformer.main;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.timur560.platformer.Platformer;
import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.core.Helper;

/**
 * Created by qwer on 30.01.15.
 */
public class Hint extends GameObject {
    protected String text;

    public Hint(Game g, Long x, Long y, String t) {
        super(g);
        shape = new Rectangle(Helper.cellsToPx(x, y)[0], Helper.cellsToPx(x, y)[1], Helper.CELL_SIZE, Helper.CELL_SIZE);
        text = t;
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {

    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        if (shape.intersects(game.getPlayer().getShape())) {

            game.getFont().drawString(
                    Helper.offsetValues((int) (Platformer.WIDTH / Platformer.ZOOM - game.getFont().getWidth(text)) / 2, 100, game.getOffset())[0],
                    Helper.offsetValues((int) (Platformer.WIDTH / Platformer.ZOOM - game.getFont().getWidth(text)) / 2, 100, game.getOffset())[1], text);
        }
    }
}
