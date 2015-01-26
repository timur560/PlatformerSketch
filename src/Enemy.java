import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;

/**
 * Created by qwer on 23.01.15.
 */
abstract class Enemy {
    protected Shape s;

    protected boolean dead;

    public Enemy(float[] vertices) {
        s = new Polygon(vertices);
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        if (!dead) g.draw(s);
    }

    public void update(GameContainer gc, int delta) throws SlickException {

    }

    public void die() {
        dead = true;
        s = null;
    }

    public Shape getShape() {
        return s;
    }

    public boolean isDead() {
        return dead;
    }
}
