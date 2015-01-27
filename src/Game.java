import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Game extends BasicGame
{
    protected Level level;
    protected Player player;

    public float[] getOffset() {
        float[] result = new float[]{0,0};

        if (player.getX() - 300 > level.getWidth() - Platformer.WIDTH) result[0] = level.getWidth() - Platformer.WIDTH;
        else if (player.getX() < 300) result[0] = 0;
        else result[0] = player.getX() - 300;

        if (player.getY() - 300> level.getHeight() - Platformer.HEIGHT) result[1] = level.getHeight() - Platformer.HEIGHT;
        else if (player.getY() < 300) result[1] = 0;
        else result[1] = player.getY() - 300;

        return result;
    }

    public Game() throws SlickException {
        super("Platformer Sketch");
    }

    public void init(GameContainer gc) throws SlickException {
        level = new Level(this);
        level.init(gc);

        player = new Player(this);
        player.init(gc);
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        // calculate offset
        float[] offset = getOffset();
        g.translate(-offset[0], -offset[1]);

        // render
        level.render(gc, g);
        player.render(gc, g);
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        level.update(gc, delta);
        player.update(gc, delta);
    }

    public Player getPlayer() {
        return player;
    }

    public Level getLevel() {
        return level;
    }
}