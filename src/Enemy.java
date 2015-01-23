import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;

/**
 * Created by qwer on 23.01.15.
 */
abstract class Enemy {
    protected Shape shape;

    public Enemy(float[] vertices) {
        shape = new Polygon(vertices);
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.draw(shape);
    }

    public void update(GameContainer gc, int delta) throws SlickException {

    }

    public Shape toShape() {
        return shape;
    }
}
