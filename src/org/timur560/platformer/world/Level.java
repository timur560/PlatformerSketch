package org.timur560.platformer.world;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;
import org.newdawn.slick.util.ResourceLoader;
import org.stringtree.json.JSONReader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.timur560.platformer.Platformer;
import org.timur560.platformer.core.Helper;
import org.timur560.platformer.entities.*;
import org.timur560.platformer.entities.enemies.*;
import org.timur560.platformer.entities.weapon.Gun;
import org.timur560.platformer.main.Game;
import org.timur560.platformer.main.Hint;

public class Level {
    protected Game game;

    // common level vars
    protected int id;
    protected float[] entryPoint = new float[]{0.0f, 0.0f};
    protected long time;
    protected String title, description;

    // zone vars
    protected List<Shape> platforms;
    protected List<Ladder> ladders;
    protected List<Enemy> enemies;
    protected List<Portal> portals;
    protected List<Heart> hearts;
    protected List<Hint> hints;
    protected float width = 2000, height = 900;
    protected Image bg, zoneImage; // tmp
    protected String effect;


    public Level(Game g, int id) {
        game = g;
        this.id = id;

        if (ResourceLoader.getResource("res/levels/" + id + "/common.json") == null) {
            System.out.println("No common.json for level #" + id);
            System.exit(0);
        }

        JSONReader jsonReader = new JSONReader();

        Object result = null;

        try {
            String jsonString = String.join("",
                    Files.readAllLines(Paths.get(ResourceLoader.getResource("res/levels/" + id + "/common.json").toURI())));
            result = jsonReader.read(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map params = (Map) result;

        title = (String) params.get("title");
        description = (String) params.get("description");
        time = ((Long) params.get("time")).longValue();

        loadZone("0");
    }

    public void loadZone(String zone) {
        if (ResourceLoader.getResource("res/levels/" + id + "/" + zone + ".json") == null) {
            System.out.println("No resources for such zone " + id + "/" + zone);
            System.exit(0);
        }

        JSONReader jsonReader = new JSONReader();

        Object result = null;

        try {
            String jsonString = String.join("",
                    Files.readAllLines(Paths.get(ResourceLoader.getResource("res/levels/" + id + "/" + zone + ".json").toURI())));
            result = jsonReader.read(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map params = (Map) result;

        // TODO
        try {
            bg = new Image(ResourceLoader.getResource("res/images/bg.png").getFile());
            zoneImage = new Image(ResourceLoader.getResource("res/images/level1.png").getFile());
        } catch (SlickException e) {
            e.printStackTrace();
        }

        width = ((Long) params.get("width")).floatValue();
        height = ((Long) params.get("height")).floatValue();
        effect = (String) params.get("effect");

        // platforms
        platforms = new ArrayList<Shape>();

        for (List<Long> vertices : (List<List<Long>>) params.get("shapes")) {
            platforms.add(Helper.cellsToPolygon(vertices.get(0), vertices.get(1), vertices.get(2), vertices.get(3)));

        }

        // ladders
        ladders = new ArrayList<Ladder>();

        for (List<Long> vertices : (List<List<Long>>) params.get("ladders")) {
            ladders.add(new Ladder(game, vertices.get(0), vertices.get(1), vertices.get(2)));
        }

        // enemies
        enemies = new ArrayList<Enemy>();

        for (Map e : ((List<Map>) params.get("enemies"))) {
            if (((List)e.get("path")).size() == 1) { // static

                Enemy enemy = new StaticEnemy(
                        game,
                        ((List<List<Long>>) e.get("path")).get(0),
                        (List<Double>) e.get("rect"),
                        (String)    e.get("type"), // etc. "snowman"
                        (boolean)   e.get("canDie"),
                        ((Long)     e.get("direction")).intValue()
                );

                if ((boolean) e.get("weapon")) {
                    enemy.setWeapon(new Gun(game, enemy), ((Long) e.get("shootDelay")).intValue());
                }
                enemies.add(enemy);
            } else  { // moving ( > 1)
                Enemy enemy = new MovingEnemy(
                        game,
                        (String) e.get("type"), // etc. "snowman"
                        (boolean) e.get("canDie"),
                        (List<List<Long>>) e.get("path"),
                        (List<Double>) e.get("rect"),
                        (Double) e.get("speed")
                );

                if ((boolean) e.get("weapon")) {
                    enemy.setWeapon(new Gun(game, enemy), ((Long) e.get("shootDelay")).intValue());
                }

                enemies.add(enemy);
            }
        }

        // portals
        portals = new ArrayList<>();

        for (Map p : ((List<Map>) params.get("portals"))) {
            if (((List<Long>) p.get("dest")).get(0) == id) {
                if (((List<Long>) p.get("dest")).get(0) == id && ((List<Long>) p.get("dest")).get(1) == 0 && ((List<Long>) p.get("dest")).get(2) == 0) {
                    entryPoint = Helper.cellsToPx(((List<Long>) p.get("pos")).get(0), ((List<Long>) p.get("pos")).get(1));
                }

                portals.add(new Portal(game, (List<Long>) p.get("pos"), (List<Long>) p.get("dest")));
            } else {
                portals.add(new Portal(game, (List<Long>) p.get("pos"), (List<Long>) p.get("dest"), (List<Long>) p.get("wall"),
                        new ActionTerminal(game, (List<Long>) p.get("terminal"))));
            }
        }

        // hearts
        hearts = new ArrayList<>();

        for (Map heart : ((List<Map>) params.get("hearts"))) {
            hearts.add(new Heart(game, ((List<Long>) heart.get("pos")).get(0), ((List<Long>) heart.get("pos")).get(1),
                    (String) heart.get("secret"), (Boolean) heart.get("fake")));

        }

        // hints
        hints = new ArrayList<>();

        for (Map hint : ((List<Map>) params.get("hints"))) {
            hints.add(new Hint(game, ((List<Long>) hint.get("pos")).get(0), ((List<Long>) hint.get("pos")).get(1),
                    (String) hint.get("text")));
        }
    }

    public void init() throws SlickException {
        for (Enemy e : enemies) e.init();
        for (Portal e : portals) e.init();
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        int i;

        for (i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).isDead()) {
                enemies.remove(i);
            }
        }

        for (Enemy e : enemies) e.update(gc, delta);
        for (Portal e : portals) e.update(gc, delta);
        for (Heart h : hearts) h.update(gc, delta);
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        float[] offset = game.getOffset();

        float bgX = offset[0] * ((width - bg.getWidth())) / (width - Platformer.WIDTH);
        float bgY = offset[1] * ((height - bg.getHeight())) / (height - Platformer.HEIGHT);

        // parallax background
        g.drawImage(bg, bgX, bgY);

        // level static objects image
        g.drawImage(zoneImage, 0, 0);

        if (Platformer.DEBUG_MODE) drawDebugLines(g, 50);

        g.setColor(Color.green);

        if (Platformer.DEBUG_MODE) for (Shape p : platforms) {
            g.draw(p);
        }

        for (Ladder l : ladders) l.render(gc, g);
        for (Enemy e : enemies) e.render(gc, g);
        for (Portal e : portals) e.render(gc, g);
        for (Heart h : hearts) h.render(gc, g);
        for (Hint h : hints) h.render(gc, g);

        if (effect.equals("snow")) {
            Helper.renderSnow(g, offset);
        }
    }

    public boolean collidesWith (Shape s) {
        if (s.getX() < 0 || s.getX() + s.getWidth() > width) return true;

        for (Shape p : platforms) {
            if (p.intersects(s)) return true;
        }

        for (Portal e : portals) {
            if (e.intersects(s)) return true;
        }

        return false;
    }

    public boolean collidesWithLadder(Shape s) {
        for (Ladder l : ladders) {
            if (l.getShape().intersects(s)) return true;
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

    public List<Portal> getPortals() {
        return portals;
    }

    public float[] getEntryPoint() {
        return entryPoint;
    }

    public long getHeartsTotal() {
        return hearts.stream().filter(h -> !h.getFake()).count();
    }

    public long getHeartsCollected() {
        return hearts.stream().filter(h -> !h.getFake() && h.getCollected()).count();
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