import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Shape;

/**
 * Created by qwer on 23.01.15.
 */
abstract class Weapon {
    protected Shape s;
    protected Player player;

    public Weapon(Player p) {
        player = p;
    }

    abstract public void init();
    abstract public void act();

    public void update(GameContainer gc, int delta) throws SlickException {

    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.draw(s);
    }
}
