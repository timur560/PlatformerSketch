package org.timur560.platformer.main;

import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.ResourceLoader;
import org.timur560.platformer.Platformer;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by timur on 27.01.15.
 */
public class Menu extends BasicGameState {
    public static int ID = 0;
    private StateBasedGame game;
    private int currentItemId = 0;
    private List<String> items = new ArrayList<String>();
    private TrueTypeFont fontSmall, fontNormal, fontBig;

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        try {
            fontSmall = new TrueTypeFont(java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream("res/fonts/CraftyGirls.ttf")).deriveFont(16f), true);
            fontNormal = new TrueTypeFont(java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream("res/fonts/CraftyGirls.ttf")).deriveFont(24f), true);
            fontBig = new TrueTypeFont(java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream("res/fonts/CraftyGirls.ttf")).deriveFont(36f), true);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        items.add("Play");
        items.add("Controls");
        items.add("Quit");
        game = stateBasedGame;
    }

    @Override
    public void update(GameContainer gc, StateBasedGame stateBasedGame, int i) throws SlickException {

        if (gc.getInput().isKeyPressed(Input.KEY_UP) || gc.getInput().isControlPressed(2)) {
            if (currentItemId > 0) {
                currentItemId--;
            }
        } else if (gc.getInput().isKeyPressed(Input.KEY_DOWN) || gc.getInput().isControlPressed(3)) {
            if (currentItemId < items.size() - 1) {
                currentItemId++;
            }
        } else if (gc.getInput().isKeyPressed(Input.KEY_ENTER) || gc.getInput().isButtonPressed(11,0)) {
            switch (currentItemId) {
                case 0: // play
                    game.enterState(org.timur560.platformer.main.Splash.ID, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
                    break;
                case 1:
                    break;
                case 2: // quit
                    System.exit(0);
                    break;
            }
        }
    }

    @Override
    public void render(GameContainer gc, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
        g.setColor(Color.white);
        fontBig.drawString(Platformer.WIDTH / 2 - fontBig.getWidth(Platformer.NAME) / 2, 50, Platformer.NAME);

        int i = 0;
        for (String s : items) {
            if (currentItemId == i) {
                fontNormal.drawString(Platformer.WIDTH / 2 - fontNormal.getWidth(s) / 2, 50 * (i + 4), s);
            } else {
                fontSmall.drawString(Platformer.WIDTH / 2 - fontSmall.getWidth(s) / 2, 50 * (i + 4), s);
            }
            i++;
        }
    }
}
