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

    public float WIDTH = 2000;
    public float HEIGHT = 900;

    public void init(GameContainer gc) throws SlickException {

        platforms = new ArrayList<Shape>();

        platforms.add(new Polygon(new float[]
                {
                        0,0,
                        50,0,
                        50,850,
                        1300,850,
                        1300,900,
                        0,900
                }));
        platforms.add(new Rectangle(1350, 750, 200, 25));
        platforms.add(new Rectangle(650, 600, 200, 25));
        platforms.add(new Rectangle(1000, 550, 200, 25));
        platforms.add(new Polygon(new float[]
            {
                    1700,550,
                    1950,550,
                1950,350,
                1800,350,
                1800,300,
                1950,300,
                    1950,0,
                    2000,0,
                    2000,600,
                    1700,600
            }));
        platforms.add(new Rectangle(200,300,100,25));
//        platforms.add(new Rectangle(500,400,100,25));
        platforms.add(new Rectangle(600,350,100,25));
        platforms.add(new Rectangle(800,300,200,25));
        platforms.add(new Rectangle(1050,250,200,25));
        platforms.add(new Rectangle(1450,450,200,25));
        platforms.add(new Rectangle(1250,150,150,25));

    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.setColor(Color.green);

        for (Shape p : platforms) {
            g.draw(p);
        }
    }

    public void update(GameContainer gc, int delta) throws SlickException {

    }

    public boolean collidesWith (Shape s) {
        for (Shape p : platforms) {
            if (p.intersects(s)) return true;
        }

        return false;
    }

}