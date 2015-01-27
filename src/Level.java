import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
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

    private Image bg; // tmp
    private SpriteSheet staticSprite;

    protected Game game;

    public Level(Game g) {
        game = g;
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

        // tmp
        try {
            bg = new Image(this.getClass().getResource("res/images/bg.png").getFile());
        } catch (SlickException e) {
            e.printStackTrace();
        }

        // static objects (ladder, ...) sprite sheet
        try {
            staticSprite = new SpriteSheet(new Image(this.getClass().getResource("res/images/static.png").getFile()), 50,50);
        } catch (SlickException e) {
            e.printStackTrace();
        }

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
            for (Long vertex : vertices ) {
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
                            (Double)movingEnemy.get("speed"), true));
                }
            }
        }

    }

    public void update(GameContainer gc, int delta) throws SlickException {

        int i = 0;
        for (i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).isDead()) {
                enemies.remove(i);
            }
        }

        for (Enemy e : enemies) {
            e.update(gc, delta);
        }
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {

        float[] offset = game.getOffset();

        // max offset = width - Platformer.WIDTH
        // width - Platformer.WIDTH / offet[0] = (width - bg.getWidth()) / bgx

        float bgX = offset[0] * ((width - bg.getWidth())) / (width - Platformer.WIDTH);
        float bgY = offset[1] * ((height - bg.getHeight())) / (height - Platformer.HEIGHT);

        g.drawImage(bg, bgX, bgY);

        if (Platformer.DEBUG_MODE) drawDebugLines(g, 50);

        g.setColor(Color.green);

        for (Shape p : platforms) {
            g.draw(p);
        }

        g.setColor(Color.yellow);

        for (final Ladder l : ladders) {
            if (Platformer.DEBUG_MODE) g.draw(l.toShape());

            int i;
            for (i = 0; i <= l.toShape().getHeight(); i += 50) {
                g.drawImage(staticSprite.getSubImage(0,0), l.toShape().getX() - 10, l.toShape().getY() + i);
            }
        }

        g.setColor(Color.red);

        for (Enemy e : enemies) {
            e.render(gc, g);
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
            if (e.getShape().intersects(s)) return true;
        }

        return false;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public float[] getEntryPoint() {
        return entryPoint;
    }

    // Draw a grid on the screen for easy positioning
    public void drawDebugLines(Graphics g, int size) {
        g.setColor(Color.darkGray);
        for (int i = 0; i < width; i += size) {
            g.drawLine(i, 0, i, width);
            g.drawLine(0,i, width, i);
        }
    }

}