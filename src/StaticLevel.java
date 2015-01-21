import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import java.util.ArrayList;
import java.util.List;

public class StaticLevel {
    private List<Shape> platforms;

    public static float WIDTH = 2000;
    public static float HEIGHT = 600;

    private float offset = 0, offsetTmp = 0;

    public void init(GameContainer gc) throws SlickException {

        float[] polygonPoints = new float[]
            {
                    0,0,
                    50,0,
                    50,550,
                    1950,550,
                    1950,350,
                    1800,350,
                    1800,300,
                    1950,300,
                    1950,0,
                    2000,0,
                    2000,600,
                    0,600
            };

        platforms = new ArrayList<Shape>();
        platforms.add(new Polygon(polygonPoints));
        platforms.add(new Rectangle(200,300,100,50));
        platforms.add(new Rectangle(500,400,100,50));
        platforms.add(new Rectangle(800,300,200,50));
        platforms.add(new Rectangle(1200,150,200,50));

    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.setColor(Color.green);

        for (Shape p : platforms) {
            g.draw(p);
        }
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        for (Shape p : platforms) {
            p.setX(p.getX() + (offsetTmp - offset));
        }
        offsetTmp = offset;
    }

    public boolean collidesWith (Shape s) {
        for (Shape p : platforms) {
            if (p.intersects(s)) return true;
        }

        return false;
    }

    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }
}