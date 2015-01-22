import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.stringtree.json.JSONReader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level {
    private List<Shape> platforms;

    private float width = 2000;
    private float height = 900;

    private Map params = new HashMap();

    public void load(String jsonPath) {

        if (this.getClass().getResource(jsonPath) == null) {
            System.out.println("No such resource file : " + jsonPath);
            System.exit(0);
        }

        JSONReader jsonReader = new JSONReader();

        Object result = null;

        try {
            String jsonString = String.join("",
                    Files.readAllLines(Paths.get(this.getClass().getResource(jsonPath).toURI())));
            result = jsonReader.read(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        params = (Map) result;

    }

    public void init(GameContainer gc) throws SlickException {

        load("res/levels/1.json");

        width = ((Long) params.get("width")).floatValue();
        height = ((Long) params.get("height")).floatValue();

        platforms = new ArrayList<Shape>();

        float[] floatArray;
        int i;

        for (List<Long> vertices : (List<List<Long>>) params.get("shapes")) {
            floatArray = new float[vertices.size()];
            i = 0;
            for (Long vertex : vertices) {
                floatArray[i++] = vertex.floatValue();
            }
            platforms.add(new Polygon(floatArray));
        }
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

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}