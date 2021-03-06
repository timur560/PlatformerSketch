package org.timur560.platformer;

import java.io.File;

import org.lwjgl.LWJGLUtil;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import org.timur560.platformer.main.Game;
import org.timur560.platformer.main.Menu;
import org.timur560.platformer.main.Splash;

public class Platformer extends StateBasedGame {
    public static boolean DEBUG_MODE = false;
    public static String NAME = "Blue-Eyed Girl";
    public static int WIDTH = 800;
    public static int HEIGHT = 600;
    // public static float ZOOM = 1.0f;
    private int currentLevel = 7;
    public static AppGameContainer app;

    public Platformer() throws SlickException {
        super(NAME);
    }

    @Override
    public void initStatesList(GameContainer gameContainer) throws SlickException {
        addState(new Menu());
        addState(new Splash());
        addState(new Game());
    }

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File(new File(System.getProperty("user.dir"), "native"),
                LWJGLUtil.getPlatformName()).getAbsolutePath());

        try {
            app = new AppGameContainer(new Platformer());
            app.setDisplayMode(WIDTH, HEIGHT, false);
            app.setVSync(false);
            app.setShowFPS(DEBUG_MODE);
            app.start();

        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }
}