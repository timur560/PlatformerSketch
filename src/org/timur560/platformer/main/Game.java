package org.timur560.platformer.main;

import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.ResourceLoader;

import org.timur560.platformer.Platformer;
import org.timur560.platformer.entities.Player;
import org.timur560.platformer.world.Level;

import java.awt.FontFormatException;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;

public class Game extends BasicGameState { // BasicGame
    protected Level level;
    protected Player player;
    protected StatusBar statusBar;
    protected SpriteSheet tileset;
    protected TrueTypeFont font;

    StateBasedGame game;

    public static int ID = 1;

    public Game() {
        try {
            tileset = new SpriteSheet(new Image(ResourceLoader.getResource("res/images/tileset1.png").getFile()), 50, 50);
            InputStream inputStream = ResourceLoader.getResourceAsStream("res/fonts/CraftyGirls.ttf");
            font = new TrueTypeFont(Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(18f), true);
        } catch (SlickException e) {
            e.printStackTrace();
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public float[] getOffset() {
        float[] result = new float[]{0,0};

        if (player.getX() - 300 / Platformer.ZOOM > level.getWidth() - Platformer.WIDTH / Platformer.ZOOM) result[0] = level.getWidth() - Platformer.WIDTH / Platformer.ZOOM;
        else if (player.getX() < 300 / Platformer.ZOOM) result[0] = 0;
        else result[0] = player.getX() - 300 / Platformer.ZOOM;

        if (player.getY() - 300 / Platformer.ZOOM > level.getHeight() - Platformer.HEIGHT / Platformer.ZOOM) result[1] = level.getHeight() - Platformer.HEIGHT / Platformer.ZOOM;
        else if (player.getY() < 300 / Platformer.ZOOM) result[1] = 0;
        else result[1] = player.getY() - 300 / Platformer.ZOOM;

        result[0] *= Platformer.ZOOM;
        result[1] *= Platformer.ZOOM;

        return result;
    }

    public Player getPlayer() {
        return player;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer gc, StateBasedGame stateBasedGame) throws SlickException {
        game = stateBasedGame;

        level = new Level(this);
        player = new Player(this);

        level.init();
        player.init();

        statusBar = new StatusBar(this);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame stateBasedGame, int delta) throws SlickException {
        level.update(gc, delta);
        player.update(gc, delta);
        statusBar.update(gc, delta);
    }

    @Override
    public void render(GameContainer gc, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
        // calculate offset
        float[] offset = getOffset();

        g.translate(-offset[0], -offset[1]);

        g.setAntiAlias(false);
        g.scale(Platformer.ZOOM, Platformer.ZOOM);

        // render
        level.render(gc, g);
        player.render(gc, g);
        statusBar.render(gc, g);
    }

    public void keyReleased(int key, char c) {
        switch (key) {
            case Input.KEY_ESCAPE:
                game.enterState(Menu.ID, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
                break;
        }
    }

    public SpriteSheet getTileset() {
        return tileset;
    }

    public TrueTypeFont getFont() {
        return font;
    }
}