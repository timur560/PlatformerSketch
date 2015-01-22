import java.io.File;

import org.lwjgl.LWJGLUtil;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

public class Platformer {

    public static boolean DEBUG_MODE = true;

    public static int WIDTH = 800;
    public static int HEIGHT = 600;

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File(new File(System.getProperty("user.dir"), "native"),
                LWJGLUtil.getPlatformName()).getAbsolutePath());

        try {
            AppGameContainer app = new AppGameContainer(new Game());
            app.setDisplayMode(WIDTH, HEIGHT, false);
            app.setVSync(true);
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

}