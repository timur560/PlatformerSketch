package org.timur560.platformer.main;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import org.timur560.platformer.Platformer;
import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.core.Helper;

/**
 * Created by qwer on 29.01.15.
 */
public class StatusBar extends GameObject {
    protected long heartsCollected = 0, heartsTotal = 0, lives = 0, time = 0;
    protected String  hint = "";

    public StatusBar(Game g) {
        super(g);
        heartsTotal = g.getLevel().getHeartsTotal();
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        heartsCollected = game.getLevel().getHeartsCollected();
//        hint = game.getLevel().getCurrentHint();
//        time = game.getLevel().getTimeLeft();
//        lives = game.getLevel().getLivesLeft();
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        float[] offset = game.getOffset();

        g.drawImage(game.getTileset().getSubImage(0, 4), Helper.offsetValues(10, 10, offset)[0], Helper.offsetValues(10, 10, offset)[1]);

        g.setColor(Color.white);
        // game.getFont().drawString(heartsCollected + "/" + heartsTotal, Helper.offsetValues(30, 10, offset)[0], Helper.offsetValues(30, 10, offset)[1]);
        game.getFont().drawString(Helper.offsetValues(40, 8, offset)[0], Helper.offsetValues(40, 8, offset)[1], heartsCollected + "/" + heartsTotal);
    }
}
