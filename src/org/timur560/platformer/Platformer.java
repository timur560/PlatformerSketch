package org.timur560.platformer;

import java.io.File;

import org.lwjgl.LWJGLUtil;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import org.timur560.platformer.main.Game;
import org.timur560.platformer.main.Menu;

public class Platformer extends StateBasedGame {
    public static boolean DEBUG_MODE = true;
    public static String NAME = "Blue-Eyed Girl";
    public static int WIDTH = 800;
    public static int HEIGHT = 600;
    public static float ZOOM = 1.0f;

    public Platformer() throws SlickException {
        super(NAME);
    }

    @Override
    public void initStatesList(GameContainer gameContainer) throws SlickException {
        addState(new Menu());
        addState(new Game());
    }

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File(new File(System.getProperty("user.dir"), "native"),
                LWJGLUtil.getPlatformName()).getAbsolutePath());

        try {
            AppGameContainer app = new AppGameContainer(new Platformer());
            app.setDisplayMode(WIDTH, HEIGHT, false);
            app.setVSync(false);
            app.setShowFPS(DEBUG_MODE);
            app.start();

        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

}