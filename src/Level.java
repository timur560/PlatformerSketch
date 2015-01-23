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
    private List<Ladder> ladders;
    private List<Enemy> enemies;

    private float width = 2000;
    private float height = 900;

    private float[] entryPoint = new float[]{0.0f, 0.0f};

    private Map params = new HashMap();

    public float[] getEntryPoint() {
        return entryPoint;
    }

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
        float[] floatArray;
        int i;

        load("res/levels/2.json");

        width = ((Long) params.get("width")).floatValue();
        height = ((Long) params.get("height")).floatValue();

        i = 0;
        for (Long ep : (List<Long>) params.get("entryPoint")) {
            entryPoint[i++] = ep.floatValue();
        }

        // platforms
        platforms = new ArrayList<Shape>();


        for (List<Long> vertices : (List<List<Long>>) params.get("shapes")) {
            floatArray = new float[vertices.size()];
            i = 0;
            for (Long vertex : vertices) {
                floatArray[i++] = vertex.floatValue();
            }
            platforms.add(new Polygon(floatArray));
        }

        // ladders
        ladders = new ArrayList<Ladder>();

        for (List<Long> vertices : (List<List<Long>>) params.get("ladders")) {
            floatArray = new float[vertices.size()];
            i = 0;
            for (Long vertex : vertices) {
                floatArray[i++] = vertex.floatValue();
            }
            ladders.add(new Ladder(floatArray));
        }

        // enemies
        enemies = new ArrayList<Enemy>();

        for (String enemyType : ((Map<String,List>) params.get("enemies")).keySet()) {
            if (enemyType.equals("static")) {
                for (Map staticEnemy : ((Map<String,List<Map>>) params.get("enemies")).get(enemyType)) {
                    List<Long> vertices = (List)staticEnemy.get("vertices");
                    floatArray = new float[vertices.size()];
                    i = 0;
                    for (Long vertex : vertices) {
                        floatArray[i++] = vertex.floatValue();
                    }
                    enemies.add(new StaticEnemy(floatArray));
                }
            } else if (enemyType.equals("moving")) {
                for (Map movingEnemy : ((Map<String,List<Map>>) params.get("enemies")).get(enemyType)) {
                    List<Long> vertices = (List)movingEnemy.get("vertices");
                    floatArray = new float[vertices.size()];
                    i = 0;
                    for (Long vertex : vertices) {
                        floatArray[i++] = vertex.floatValue();
                    }

                    enemies.add(new MovingEnemy(floatArray, (List<List<Long>>)movingEnemy.get("path"),
                            (Double)movingEnemy.get("speed")));
                }
            }
        }

    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.setColor(Color.green);

        for (Shape p : platforms) {
            g.draw(p);
        }

        g.setColor(Color.yellow);

        for (Ladder l : ladders) {
            g.draw(l.toShape());
        }

        g.setColor(Color.red);

        for (Enemy e : enemies) {
            g.draw(e.toShape());
        }
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        for (Enemy e : enemies) {
            e.update(gc, delta);
        }
    }

    public boolean collidesWith (Shape s) {
        for (Shape p : platforms) {
            if (p.intersects(s)) return true;
        }

        return false;
    }

    public boolean collidesWithLadder(Shape s) {
        for (Ladder l : ladders) {
            if (l.toShape().intersects(s)) return true;
        }

        return false;
    }

    public boolean collidesWithEnemie(Shape s) {
        for (Enemy e : enemies) {
            if (e.toShape().intersects(s)) return true;
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