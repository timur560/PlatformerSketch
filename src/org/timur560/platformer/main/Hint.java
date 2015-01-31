package org.timur560.platformer.main;

import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.font.effects.ShadowEffect;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.util.ResourceLoader;
import org.timur560.platformer.Platformer;
import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.core.Helper;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

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

            g.setColor(new Color(255, 255, 25, 0.7f));

            g.fill(new RoundedRectangle(
                    Helper.offsetValues((int) (Platformer.WIDTH / Platformer.ZOOM - game.getFont().getWidth(text)) / 2, 100, game.getOffset())[0] - 20,
                    Helper.offsetValues((int) (Platformer.WIDTH / Platformer.ZOOM - game.getFont().getWidth(text)) / 2, 100, game.getOffset())[1] - 20,
                    game.getFont().getWidth(text) + 40,
                    game.getFont().getHeight() + 40,
                    10
            ));

            game.getFont().drawString(
                    Helper.offsetValues((int) (Platformer.WIDTH / Platformer.ZOOM - game.getFont().getWidth(text)) / 2, 100, game.getOffset())[0],
                    Helper.offsetValues((int) (Platformer.WIDTH / Platformer.ZOOM - game.getFont().getWidth(text)) / 2, 100, game.getOffset())[1],
                    text,
                    Color.black);
        }
    }
}
