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

import org.timur560.platformer.entities.*;
import org.timur560.platformer.entities.enemies.*;
import org.timur560.platformer.main.Game;

public class Level {
    protected Game game;

    protected int id, currentZone;
    protected long time, startTime;
    protected String title, description;
    protected List<Zone> zones;

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

        // init time limit
        time = ((Long) params.get("time")).longValue() * 60 * 1000;
        startTime = System.currentTimeMillis();

        zones = new ArrayList<>();

        int i;
        for (i = 0; i < (Long) params.get("zones"); i++) zones.add(new Zone(game, this, i));

    }

    public void init() throws SlickException {
        for (Zone z : zones) z.init();
        goToZone(0);
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        zones.get(currentZone).update(gc, delta);
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        zones.get(currentZone).render(gc, g);
    }

    public void goToZone(int z) {
        goToZone(z, 0);
    }

    public void goToZone(int z, int portal) {
        currentZone = z;
        Portal p = zones.get(z).getPortals().get(portal);
        game.getPlayer().getShape().setX(p.getPos()[0]);
        game.getPlayer().getShape().setY(p.getPos()[1]);
    }

    public boolean collidesWith (Shape s) {
        Zone z = zones.get(currentZone);

        if (s.getX() < 0 || s.getX() + s.getWidth() > z.getWidth()) return true;
        for (Shape p : z.getPlatforms()) if (p.intersects(s)) return true;
        for (Portal p : z.getPortals()) if (p.intersects(s)) return true;

        return false;
    }

    public MovingPlatform collidesWighMovingPlatform (Shape s) {
        for (MovingPlatform mp : zones.get(currentZone).getMovingPlatforms()) {
            if (mp.getShape().intersects(s)) return mp;
        }

        return null;
    }

    public MovingBlock collidesWithMovingBlock(Shape s) {
        for (MovingBlock mb : zones.get(currentZone).getMovingBlocks()) {
            if (mb.getShape().intersects(s)) return mb;
        }

        return null;
    }

    public Ladder collidesWithLadder(Shape s) {
        for (Ladder l : zones.get(currentZone).getLadders()) {
            if (l.getShape().intersects(s)) return l;
        }

        return null;
    }

    public boolean collidesWithEnemie(Shape s) {
        for (Enemy e : zones.get(currentZone).getEnemies()) {
            if (e.getShape().intersects(s)) return true;
        }

        return false;
    }

    public int getId() {
        return id;
    }

    public float getWidth() {
        return zones.get(currentZone).getWidth();
    }

    public float getHeight() {
        return zones.get(currentZone).getHeight();
    }

    public List<Enemy> getEnemies() {
        return zones.get(currentZone).getEnemies();
    }

    public List<Portal> getPortals() {
        return zones.get(currentZone).getPortals();
    }

    public long getHeartsTotal() {
        long heartsTotal = 0;

        for (Zone z : zones) {
            heartsTotal += z.getHearts().stream().filter(h -> !h.getFake()).count();
        }

        return heartsTotal;
    }

    public long getHeartsCollected() {
        long heartsCollected = 0;

        for (Zone z : zones) {
            heartsCollected += z.getHearts().stream().filter(h -> !h.getFake() && h.getCollected()).count();
        }

        return heartsCollected;
    }

    public Zone getZone() {
        return zones.get(currentZone);
    }

    public long getTimeLeft() {
        return time - (System.currentTimeMillis() - startTime);
    }
}