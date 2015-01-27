import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

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

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        items.add("Play");
        items.add("Quit");
        game = stateBasedGame;
    }

    @Override
    public void update(GameContainer gc, StateBasedGame stateBasedGame, int i) throws SlickException {

    }

    public void keyReleased(int key, char c) {
        switch(key) {
            case Input.KEY_UP:
                if (currentItemId > 0) {
                    currentItemId--;
                }
                break;
            case Input.KEY_DOWN:
                if (currentItemId < items.size() - 1) {
                    currentItemId++;
                }
                break;
            case Input.KEY_ENTER:
                switch (currentItemId) {
                    case 0:
                        game.enterState(Game.ID, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
                        break;
                    case 1:
                        System.exit(0);
                        break;
                }

                break;
            default:
                break;
        }
    }

    @Override
    public void render(GameContainer gc, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
        g.setColor(Color.white);
        g.drawString(Platformer.NAME, Platformer.WIDTH / 2 - g.getFont().getWidth(Platformer.NAME) / 2, 10);

        int i = 0;
        for (String item : items) {
            if (currentItemId == i) {
                g.setColor(Color.white);
                g.drawString(item, Platformer.WIDTH / 2 - g.getFont().getWidth(item) / 2, 50 * (i+2));
            } else {
                g.setColor(Color.blue);
                g.drawString(item, Platformer.WIDTH / 2 - g.getFont().getWidth(item) / 2, 50 * (i+2));
            }
            i++;
        }
    }
}
