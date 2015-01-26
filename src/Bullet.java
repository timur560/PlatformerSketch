import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import java.util.List;

/**
 * Created by timur on 25.01.15.
 */
public class Bullet {
    protected Shape s;

    protected float x1, y1, x2, y2;
    protected float t = 0, speed = 0.6f;
    protected boolean dead = false;
    protected Weapon weapon;

    public Bullet(Weapon w, float x1, float y1, float x2 ,float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        weapon = w;
        s = new Rectangle(x1, y1, 20, 20);
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        // check if bullet intersects wall
        // TODO

        t += speed / delta;

        s.setX((1 - t) * x1 + t * x2);
        s.setY((1 - t) * y1 + t * y2);

        if (t > 1) {
            die();
        }
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.draw(s);
    }

    public Shape getShape() {
        return s;
    }

    public void die() {
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }
}
