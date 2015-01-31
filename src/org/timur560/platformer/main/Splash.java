package org.timur560.platformer.main;

import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.ResourceLoader;
import org.stringtree.json.JSONReader;
import org.timur560.platformer.Platformer;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by timur on 31.01.15.
 */
public class Splash extends BasicGameState {
    public static int ID = 2;

    private StateBasedGame game;
    private GameContainer container;
    private int level;
    private String title, description;
    private TrueTypeFont fontSmall, fontNormal, fontBig;

    @Override
    public int getID() {
        return 2;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        game = stateBasedGame;
        container = gameContainer;
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        super.enter(container, game);

        level = ((Platformer) game).getCurrentLevel();

        if (ResourceLoader.getResource("res/levels/" + level + "/common.json") == null) {
            System.out.println("No common.json for level #" + level);
            System.exit(0);
        }

        JSONReader jsonReader = new JSONReader();

        Object result = null;

        try {
            String jsonString = String.join("",
                    Files.readAllLines(Paths.get(ResourceLoader.getResource("res/levels/" + level + "/common.json").toURI())));
            result = jsonReader.read(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map params = (Map) result;

        title = (String) params.get("title");
        description = (String) params.get("description");

        try {
            fontSmall = new TrueTypeFont(java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream("res/fonts/CraftyGirls.ttf")).deriveFont(16f), true);
            fontNormal = new TrueTypeFont(java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream("res/fonts/CraftyGirls.ttf")).deriveFont(24f), true);
            fontBig = new TrueTypeFont(java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream("res/fonts/CraftyGirls.ttf")).deriveFont(36f), true);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void keyReleased(int key, char c) {
        switch (key) {
            case Input.KEY_ENTER:
                try {
                    int gameId = org.timur560.platformer.main.Game.ID;
                    game.getState(gameId).init(container, game);
                    game.enterState(gameId, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
                } catch (SlickException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int i) throws SlickException {

    }

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        g.setColor(Color.white);
        fontBig.drawString(Platformer.WIDTH / 2 - fontBig.getWidth(title) / 2, 200, title);
        fontNormal.drawString(Platformer.WIDTH / 2 - fontNormal.getWidth(description) / 2, 300, description);
        fontSmall.drawString(Platformer.WIDTH / 2 - fontSmall.getWidth("Press Enter") / 2, 500, "Press Enter");
    }

}
