package org.timur560.platformer.main;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.newdawn.slick.*;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.pbuffer.FBOGraphics;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;

public class Game extends BasicGameState { // BasicGame
    protected Level level;
    protected Player player;
    protected StatusBar statusBar;
    protected List<SpriteSheet> tilesets;
    protected TrueTypeFont font, fontBig, fontSmall;
    protected Map<String, Audio> sounds;

    public StateBasedGame game;
    private float sfGain = 0.6f;

    public static int ID = 1;

    public Game() {
        try {
            // tilesets
            tilesets = new ArrayList<>();
            tilesets.add(new SpriteSheet(new Image(ResourceLoader.getResource("res/images/tileset1.png").getFile()), 50, 50));
            tilesets.add(new SpriteSheet(new Image(ResourceLoader.getResource("res/images/tileset2.png").getFile()), 50, 50));
            tilesets.add(new SpriteSheet(new Image(ResourceLoader.getResource("res/images/tileset3.png").getFile()), 50, 50));

            // sounds
            sounds = new HashMap<>();
            sounds.put("jump", AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/jump.ogg")));
            sounds.put("enemyshoot", AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/enemyshoot.ogg")));
            sounds.put("playershoot", AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/playershoot.ogg")));
            sounds.put("pickup", AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/pickup.ogg")));
            sounds.put("die", AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/die.ogg")));
            sounds.put("damage", AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/damage.ogg")));
            sounds.put("open", AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/open.ogg")));
            sounds.put("openterminal", AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/openterminal.ogg")));
            sounds.put("teleport", AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/sounds/teleport.ogg")));

            // fonts
            font = new TrueTypeFont(Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream("res/fonts/CraftyGirls.ttf")).deriveFont(18f), true);
            fontBig = new TrueTypeFont(Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream("res/fonts/CraftyGirls.ttf")).deriveFont(22f), true);
            fontSmall = new TrueTypeFont(Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream("res/fonts/CraftyGirls.ttf")).deriveFont(14f), true);
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

        if (player.getX() - 300 / getLevel().getZone().getZoom() > level.getWidth() - Platformer.WIDTH / getLevel().getZone().getZoom()) result[0] = level.getWidth() - Platformer.WIDTH / getLevel().getZone().getZoom();
        else if (player.getX() < 300 / getLevel().getZone().getZoom()) result[0] = 0;
        else result[0] = player.getX() - 300 / getLevel().getZone().getZoom();

        if (player.getY() - 300 / getLevel().getZone().getZoom() > level.getHeight() - Platformer.HEIGHT / getLevel().getZone().getZoom()) result[1] = level.getHeight() - Platformer.HEIGHT / getLevel().getZone().getZoom();
        else if (player.getY() < 300 / getLevel().getZone().getZoom()) result[1] = 0;
        else result[1] = player.getY() - 300 / getLevel().getZone().getZoom();

        result[0] *= getLevel().getZone().getZoom();
        result[1] *= getLevel().getZone().getZoom();

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

        level = new Level(this, ((Platformer) game).getCurrentLevel());
        player = new Player(this);

        level.init();
        player.init();

        statusBar = new StatusBar(this);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame stateBasedGame, int delta) throws SlickException {
        if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
            game.enterState(Menu.ID, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
        }

        if (gc.getInput().isKeyPressed(Input.KEY_F) || gc.getInput().isButtonPressed(6, 0)) {
            try {
                Platformer.app.setDisplayMode(Platformer.WIDTH, Platformer.HEIGHT, !Platformer.app.isFullscreen());
            } catch (SlickException e) {
                e.printStackTrace();
            }
        }

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
        g.scale(getLevel().getZone().getZoom(), getLevel().getZone().getZoom());



        // render
        level.render(gc, g);
        player.render(gc, g);
        statusBar.render(gc, g);
    }

    public SpriteSheet getTileset(int id) {
        return tilesets.get(id - 1);
    }

    public Audio getSound(String s) {
        return sounds.get(s);
    }

    public TrueTypeFont getFont(String size) {
        if (size.equals("big")) {
            return fontBig;
        } else if (size.equals("small")) {
            return fontSmall;
        } else {
            return font;
        }
    }

    public TrueTypeFont getFont() {
        return getFont("");
    }

    public void goToLevel(int level) {
        ((Platformer) game).setCurrentLevel(level);
        game.enterState(Splash.ID, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
    }

    public float getSfGain() {
        return sfGain;
    }
}