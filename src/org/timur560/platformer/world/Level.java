package org.timur560.platformer.world;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;
import org.stringtree.json.JSONReader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.timur560.platformer.Platformer;
import org.timur560.platformer.entities.*;
import org.timur560.platformer.entities.enemies.*;
import org.timur560.platformer.main.Game;

public class Level {
    private List<Shape> platforms;
    private List<Ladder> ladders;
    private List<Enemy> enemies;
    private List<Exit> exits;

    private float width = 2000;
    private float height = 900;

    private float[] entryPoint = new float[]{0.0f, 0.0f};

    private Map params = new HashMap();

    private Image bg; // tmp
    private SpriteSheet staticSprite;

    protected Game game;

    public Level(org.timur560.platformer.main.Game g) {
        game = g;
        load("2");

        float[] floatArray;
        int i;

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
                    enemies.add(new StaticEnemy(game, floatArray));
                }
            } else if (enemyType.equals("moving")) {
                for (Map movingEnemy : ((Map<String,List<Map>>) params.get("enemies")).get(enemyType)) {
                    List<Long> vertices = (List)movingEnemy.get("vertices");
                    floatArray = new float[vertices.size()];
                    i = 0;
                    for (Long vertex : vertices) {
                        floatArray[i++] = vertex.floatValue();
                    }

                    enemies.add(new MovingEnemy(game, floatArray, (List<List<Long>>)movingEnemy.get("path"),
                            (Double)movingEnemy.get("speed"), true));
                }
            }
        }

        // exits
        exits = new ArrayList<Exit>();

        for (Map exit : ((List<Map>) params.get("exits"))) {
            exits.add(new Exit(game, (List<Long>) exit.get("wall"), (List<Long>) exit.get("door"),
                    new ActionTerminal(game, (List<Long>) exit.get("terminal"))));
        }

    }

    public void load(String level) {

        if (this.getClass().getResource("/res/levels/" + level + ".json") == null) {
            System.out.println("No resources for level " + level);
            System.exit(0);
        }

        JSONReader jsonReader = new JSONReader();

        Object result = null;

        try {
            String jsonString = String.join("",
                    Files.readAllLines(Paths.get(this.getClass().getResource("/res/levels/" + level + ".json").toURI())));
            result = jsonReader.read(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        params = (Map) result;

        // tmp
        try {
            bg = new Image(this.getClass().getResource("/res/images/bg.png").getFile());
        } catch (SlickException e) {
            e.printStackTrace();
        }

        // tmp
        // static objects (ladder, ...) sprite sheet
        try {
            staticSprite = new SpriteSheet(new Image(this.getClass().getResource("/res/images/static.png").getFile()), 50,50);
        } catch (SlickException e) {
            e.printStackTrace();
        }

    }

    public void init() throws SlickException {
        for (Enemy e : enemies) {
            e.init();
        }

        for (Exit e : exits) {
            e.init();
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

        for (Exit e : exits) {
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

        for (Exit e : exits) {
            e.render(gc, g);
        }
    }

    public boolean collidesWith (Shape s) {
        for (Shape p : platforms) {
            if (p.intersects(s)) return true;
        }

        for (Exit e : exits) {
            if (e.intersects(s)) return true;
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

    public List<Exit> getExits() {
        return exits;
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