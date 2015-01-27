import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import java.util.List;

/**
 * Created by timur on 27.01.15.
 */
public class Exit {
    private ActionTerminal terminal;

    private Game game;

    private Shape wallShape, doorShape;

    private String nextLevel;

    public Exit(Game g, List<Long> wall, List<Long> door, ActionTerminal at) {
        game = g;
        terminal = at;
        wallShape = new Rectangle(wall.get(0) + 15, wall.get(1), 20, wall.get(2));
        doorShape = new Rectangle(door.get(0), door.get(1), 50, 100);
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        if (gc.getInput().isKeyDown(Input.KEY_C)) {
            System.out.println(123);
            if (terminal.getShape().intersects(game.getPlayer().getShape())) {
                terminal.toggle();
            }
        }
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        if (terminal.getState() == ActionTerminal.STATE_CLOSED) {
            g.draw(wallShape);
        }

        g.setColor(Color.black);
        g.fill(doorShape);

        terminal.render(gc, g);
    }

}
