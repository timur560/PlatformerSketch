import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class Game extends BasicGameState { // BasicGame
    protected Level level;
    protected Player player;

    StateBasedGame game;

    public static int ID = 1;

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

        level.init(gc);
        player.init(gc);
    }

    @Override
    public void render(GameContainer gc, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
        // calculate offset
        float[] offset = getOffset();
        g.translate(-offset[0], -offset[1]);

        // render
        level.render(gc, g);
        player.render(gc, g);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame stateBasedGame, int delta) throws SlickException {
        level.update(gc, delta);
        player.update(gc, delta);
    }

    public void keyReleased(int key, char c) {
        switch (key) {
            case Input.KEY_ESCAPE:
                game.enterState(Menu.ID, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
                break;
        }
    }
}