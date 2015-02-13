package org.timur560.platformer.world;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.util.ResourceLoader;
import org.stringtree.json.JSONReader;
import org.timur560.platformer.Platformer;
import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.core.Helper;
import org.timur560.platformer.entities.ActionTerminal;
import org.timur560.platformer.entities.Heart;
import org.timur560.platformer.entities.Portal;
import org.timur560.platformer.entities.enemies.Enemy;
import org.timur560.platformer.entities.enemies.MovingEnemy;
import org.timur560.platformer.entities.enemies.StaticEnemy;
import org.timur560.platformer.entities.weapon.Gun;
import org.timur560.platformer.main.Game;
import org.timur560.platformer.main.Hint;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by timur560 on 01.02.15.
 */
public class Zone extends GameObject {
    protected int id, tileset;
    protected Level level;
    protected float[] entryPoint = new float[]{0.0f, 0.0f};
    protected List<Shape> platforms;
    protected List<MovingPlatform> movingPlatforms;
    protected List<DisappearingPlatform> disappearingPlatforms;
    protected List<MovingBlock> movingBlocks;
    protected List<Ladder> ladders;
    protected List<Enemy> enemies;
    protected List<Portal> portals;
    protected List<Heart> hearts;
    protected List<Hint> hints;
    protected float width, height, zoom = 1.0f;
    protected Image bg, cover; // tmp
    protected String effect;

    public Zone(Game g, Level l, int zoneId) {
        super(g);

        id = zoneId;
        level = l;

        if (ResourceLoader.getResource("res/levels/" + level.getId() + "/" + id + ".json") == null) {
            System.out.println("No resources for such zone " + level.getId() + "/" + id);
            System.exit(0);
        }

        JSONReader jsonReader = new JSONReader();

        Object result = null;

        try {
            String jsonString = String.join("",
                    Files.readAllLines(Paths.get(ResourceLoader.getResource("res/levels/" + level.getId() + "/" + id + ".json").toURI())));
            result = jsonReader.read(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map params = (Map) result;

        try {
            bg = new Image(ResourceLoader.getResource("res/images/" + level.getId() + "/" + id + "/bg.png").getFile());
            cover = new Image(ResourceLoader.getResource("res/images/" + level.getId() + "/" + id + "/cover.png").getFile());
        } catch (SlickException e) {
            e.printStackTrace();
        }

        width = Helper.cellsToPx(((Long) params.get("width")).floatValue(), ((Long) params.get("height")).floatValue())[0];
        height = Helper.cellsToPx(((Long) params.get("width")).floatValue(), ((Long) params.get("height")).floatValue())[1];
        if (width < Platformer.WIDTH && height < Platformer.HEIGHT) {
            if (Platformer.WIDTH - width > Platformer.HEIGHT - height) {
                zoom = (float) Platformer.WIDTH / width;
            } else {
                zoom = (float) Platformer.HEIGHT / height;
            }
        } else if (width < Platformer.WIDTH) {
            zoom = (float) Platformer.WIDTH / width;
        } else if (height < Platformer.HEIGHT) {
            zoom = (float) Platformer.HEIGHT / height;
        }

        effect = (String) params.get("effect");
        tileset = ((Long) params.get("tileset")).intValue();

        // platforms
        platforms = new ArrayList<Shape>();

        for (List<Long> vertices : (List<List<Long>>) params.get("shapes")) {
            platforms.add(Helper.cellsToPolygon(vertices.get(0), vertices.get(1), vertices.get(2), vertices.get(3)));

        }

        // ladders
        ladders = new ArrayList<Ladder>();

        if (params.get("ladders") != null) for (List<Long> vertices : (List<List<Long>>) params.get("ladders")) {
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
                        (String)    e.get("type"), // "snowman | snowflake | none"
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
            if (((List<Long>) p.get("dest")).get(0) == level.getId()) {
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

        if (params.get("hints") != null) for (Map hint : ((List<Map>) params.get("hints"))) {
            hints.add(new Hint(game, ((List<Long>) hint.get("pos")).get(0), ((List<Long>) hint.get("pos")).get(1),
                    (String) hint.get("text")));
        }

        // moving platforms
        movingPlatforms = new ArrayList<>();

        for (Map mp : ((List<Map>) params.get("movingPlatforms"))) {
            movingPlatforms.add(new MovingPlatform(game, (List<List<Long>>) mp.get("path"), (Long) mp.get("width"), (Double) mp.get("speed")));
        }

        // disappearing platforms
        disappearingPlatforms = new ArrayList<>();

        if (params.get("disappearingPlatforms") != null) for (List<Long> dp : ((List<List<Long>>) params.get("disappearingPlatforms"))) {
            disappearingPlatforms.add(new DisappearingPlatform(game, dp.get(0), dp.get(1), dp.get(2)));
        }

        // moving blocks
        movingBlocks = new ArrayList<>();

        if (params.get("movingBlocks") != null) for (List mb : ((List<List>) params.get("movingBlocks"))) {
            movingBlocks.add(new MovingBlock(game, (Long) mb.get(0), (Long) mb.get(1)));
        }
    }

    public void init() throws SlickException {
        for (Enemy e : enemies) e.init();
        for (Portal p : portals) p.init();
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
        for (MovingPlatform mp: movingPlatforms) mp.update(gc, delta);
        for (DisappearingPlatform dp: disappearingPlatforms) dp.update(gc, delta);
        for (MovingBlock mb: movingBlocks) mb.update(gc, delta);
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {


        float[] offset = game.getOffset();

        float bgX = (width == Platformer.WIDTH) ? 0
                : offset[0] * ((width - bg.getWidth())) / (width - Platformer.WIDTH);
        float bgY = (height == Platformer.HEIGHT) ? 0
                : offset[1] * ((height - bg.getHeight())) / (height - Platformer.HEIGHT);

        // parallax background
        g.drawImage(bg, bgX / getZoom(), bgY / getZoom());

        // level static objects image
        g.drawImage(cover, 0, 0);

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
        for (MovingPlatform mp: movingPlatforms) mp.render(gc, g);
        for (DisappearingPlatform dp: disappearingPlatforms) dp.render(gc, g);
        for (MovingBlock mb: movingBlocks) mb.render(gc, g);

        if (effect.equals("snow")) {
            Helper.renderSnow(g, offset);
        } else if (effect.equals("rain")) {
            Helper.renderRain(g, offset, getZoom());
        }

    }

    public int getId() {
        return id;
    }

    public List<Portal> getPortals() {
        return portals;
    }

    public List<Shape> getPlatforms() {
        return platforms;
    }

    public float getWidth() {
        return width;
    }

    public List<MovingPlatform> getMovingPlatforms() {
        return movingPlatforms;
    }

    public List<DisappearingPlatform> getDisappearingPlatforms() {
        return disappearingPlatforms;
    }

    public List<MovingBlock> getMovingBlocks() {
        return movingBlocks;
    }

    public List<Ladder> getLadders() {
        return ladders;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public float getHeight() {
        return height;
    }

    public List<Heart> getHearts() {
        return hearts;
    }

    // Draw a grid on the screen for easy positioning
    public void drawDebugLines(Graphics g, int size) {
        g.setColor(Color.darkGray);
        for (int i = 0; i < height; i += size) {
            g.drawLine(0,i, width, i);
        }
        for (int i = 0; i < width; i += size) {
            g.drawLine(i, 0, i, height);
        }
    }

    public int getTileset() {
        return tileset;
    }

    public float getZoom() {
        return zoom;
    }
}
