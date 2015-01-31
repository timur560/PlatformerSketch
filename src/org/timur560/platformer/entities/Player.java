package org.timur560.platformer.entities;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.ResourceLoader;

import org.timur560.platformer.Platformer;
import org.timur560.platformer.core.Active;
import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.core.Helper;
import org.timur560.platformer.entities.weapon.Gun;
import org.timur560.platformer.entities.weapon.Weapon;
import org.timur560.platformer.main.Splash;
import org.timur560.platformer.world.Level;
import org.timur560.platformer.main.Game;
import org.timur560.platformer.world.MovingBlock;
import org.timur560.platformer.world.MovingPlatform;

import java.util.List;

public class Player extends GameObject implements Active {
    protected float gravity       = 0.7f;
    protected float jumpStrength  = -13f;
    protected float speed         = 3;
    protected float currentSpeed  = 0;
    protected float inertion      = 0.9f;
    protected float vX            = 0;
    protected float vY            = 0;
    protected int iterations    = 5,
        direction               = RIGHT,
        health                  = 10;
    protected boolean dead        = false;
    protected long prevDecreaseHealh = 0,
        decreaseHealthDelay = 1000,
        prevTeleport = 0,
        teleportDelay = 400;

    private Animation moveLeft, moveRight, stayLeft, stayRight, jumpRight, jumpLeft, moveLadder, stayLadder, current;
    private Weapon weapon;

    // teleportation vars
    private boolean animateTeleport;
    private float[] teleportFrom;
    private float[] teleportTo;
    private float t = 0, teleportSpeed = 0.4f;

    public Player(Game g) throws SlickException {
        super(g);
        shape = new Rectangle(game.getLevel().getEntryPoint()[0], game.getLevel().getEntryPoint()[1], 30, 45);
        setWeapon(new Gun(game, this));
    }

    public void init() throws SlickException {
        SpriteSheet sheet;
        sheet = new SpriteSheet(new Image(ResourceLoader.getResource("res/images/player_sprite.png").getFile()), 45, 45);
        int[] animationSpeed = new int[4];
        for (int i = 0; i <= 3; i++) animationSpeed[i] = 100;

        moveLeft = new Animation(sheet, new int[]{0,1,1,1,0,1,2,1}, animationSpeed);
        moveRight = new Animation(sheet, new int[]{0,0,1,0,0,0,2,0}, animationSpeed);
        stayLeft = new Animation(sheet, new int[]{0,1}, new int[]{200});
        stayRight = new Animation(sheet, new int[]{0,0}, new int[]{200});
        jumpLeft = new Animation(sheet, new int[]{1,1}, new int[]{200});
        jumpRight = new Animation(sheet, new int[]{1,0}, new int[]{200});
        moveLadder = new Animation(sheet, new int[]{0,2,2,2,1,2,2,2}, animationSpeed);
        stayLadder = new Animation(sheet, new int[]{2,2}, new int[]{200});

        current = stayRight;
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        if (animateTeleport) {
            t += teleportSpeed / delta;

            shape.setX((1 - t) * teleportFrom[0] + t * teleportTo[0]);
            shape.setY((1 - t) * teleportFrom[1] + t * teleportTo[1]);

            if (t > 1) {
                t = 0;
                animateTeleport = false;
            }
            else return;
        }

        Level level = game.getLevel();

        if (dead) {
            shape.setX(level.getEntryPoint()[0]);
            shape.setY(level.getEntryPoint()[1]);
            dead = false;
            return;
        }

        // ladder collision
        if (gc.getInput().isKeyDown(Input.KEY_UP)) {
            direction = UP;
            current = moveLadder;
            if (level.collidesWithLadder(shape)) {
                shape.setY(shape.getY() - speed);
            } else {
                current = stayRight;
            }
            if (level.collidesWith(shape)) {
                shape.setY(shape.getY() + speed);
            }
        } else if (gc.getInput().isKeyDown(Input.KEY_DOWN)) {
            direction = DOWN;
            shape.setY(shape.getY() + speed);
            current = moveLadder;
            if (level.collidesWith(shape)) {
                shape.setY(shape.getY() - speed);
                current = stayRight;
            }
        } else if (level.collidesWithLadder(shape)) {
            current = stayLadder;
        }

        // Y Movement-Collisions
        float vYtemp = vY / iterations;

        for (int i = 0; i < iterations; i++) {
            shape.setY(shape.getY() + vYtemp);

            MovingPlatform mp = level.collidesWighMovingPlatform(shape);
            MovingBlock mb = level.collidesWithMovingBlock(shape);

            if (level.collidesWith(shape)
                    || mp != null
                    || mb != null
                    || level.collidesWithLadder(shape)) {

                shape.setY(shape.getY() - vYtemp);

                if (mp != null) {
                    if (shape.getY() < mp.getShape().getY()) shape.setY(mp.getShape().getY() - shape.getHeight());
                    else shape.setY(mp.getShape().getY() + mp.getShape().getHeight() + 2);
                }
                vY = 0;
                break;
            }
        }

        // Y acceleration
        vY += gravity;
        if (gc.getInput().isKeyDown(Input.KEY_Z)) { // jump
            shape.setY(shape.getY() + 0.5f);

            MovingBlock mb = level.collidesWithMovingBlock(shape);

            if (level.collidesWith(shape)
                    || level.collidesWithLadder(shape)
                    || mb != null
                    || level.collidesWighMovingPlatform(shape) != null) {
                vY = jumpStrength;
            }

            if (level.collidesWithLadder(shape)) {
                current = stayLadder;
            }

            shape.setY(shape.getY() - 0.5f);
        }

        // X acceleration
        if (gc.getInput().isKeyDown(Input.KEY_LEFT)) {
            direction = LEFT;
            current = ((int) vY == 0) ? moveLeft : jumpLeft;
            if (currentSpeed >= -speed) {
                currentSpeed -= inertion;
            }
        } else if (gc.getInput().isKeyDown(Input.KEY_RIGHT)) {
            direction = RIGHT;
            current = ((int) vY == 0) ? moveRight : jumpRight;
            if (currentSpeed <= speed) {
                currentSpeed += inertion;
            }
        } else {
            if (currentSpeed < 0) {
                if (currentSpeed > -inertion) {
                    currentSpeed = 0;
                    current = stayLeft;
                }
                else currentSpeed += inertion;
            } else if (currentSpeed > 0) {
                if (currentSpeed < inertion) {
                    currentSpeed = 0;
                    current = stayRight;
                }
                else currentSpeed -= inertion;
            }
        }
        vX = currentSpeed;

        // X Movement-Collisions
        float vXtemp = vX/ iterations;

        for (int i = 0; i < iterations; i++) {
            shape.setX( shape.getX() + vXtemp );

            MovingPlatform mp = level.collidesWighMovingPlatform(shape);

            if (level.collidesWith(shape)) {
                shape.setX( shape.getX() - vXtemp );
                vX = 0;
            } else if (mp != null) {
                if (shape.getY() + shape.getHeight() > mp.getShape().getY()) {
                    shape.setX( shape.getX() - vXtemp );
                    vX = 0;
                }
            }
        }

        // teleport
        if (gc.getInput().isKeyDown(Input.KEY_C)
                && (prevTeleport == 0 || System.currentTimeMillis() > prevTeleport + teleportDelay)) {
            for (Portal p : game.getLevel().getPortals()) {
                if (p.getPortalShape().intersects(shape)) {
                    if (p.getDest().get(0) == game.getLevel().getId()) { // if current level portal
                        if (p.getDest().get(1) == game.getLevel().getZone()) { // if current zone portal
                            float[] pos = game.getLevel().getPortals().get(p.getDest().get(2).intValue()).getPos();
                            teleport(shape.getX(), shape.getY(), pos[0] + 10, pos[1]);
                            break;
                        } else { // go to another zone in current level
                            game.getLevel().loadZone(
                                    p.getDest().get(1).intValue(), // zone
                                    p.getDest().get(2).intValue()); // portal
                        }
                    } else { // load another level

                        System.out.println(p.getDest().get(0) + "  " + ((Platformer) game.game).getCurrentLevel());

                        ((Platformer) game.game).setCurrentLevel(p.getDest().get(0).intValue());

                        System.out.println(p.getDest().get(0) + "  " + ((Platformer) game.game).getCurrentLevel());

                        game.game.enterState(Splash.ID, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
                    }
                }
            }
            prevTeleport = System.currentTimeMillis();
        }

        // enemies collision
        if (level.collidesWithEnemie(shape)) {
            decreaseHealth();
            vY = jumpStrength;
        }

        // die if dropped in gap
        if (shape.getY() > level.getHeight()) {
            die();
        }

        // shoot
        if (gc.getInput().isKeyDown(Input.KEY_X)) {
            weapon.act();
        }

        weapon.update(gc, delta);
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.setColor(Color.blue);
        if (Platformer.DEBUG_MODE) g.draw(shape);
        g.drawAnimation(current, shape.getX() - 7, shape.getY());

        weapon.render(gc, g);
    }

    public void setWeapon(Weapon w) throws SlickException {
        weapon = w;
        w.init();
    }

    private void teleport(float x1, float y1, float x2, float y2) {
        teleportFrom = new float[]{x1, y1};
        teleportTo = new float[]{x2, y2};
        animateTeleport = true;
    }

    public void decreaseHealth() {
        if (prevDecreaseHealh == 0 || System.currentTimeMillis() > prevDecreaseHealh + decreaseHealthDelay) {
            health--;
            if (health <= 0) die();
            prevDecreaseHealh = System.currentTimeMillis();
        }
    }

    public void die() {
        dead = true;
        health = 10;
        game.game.enterState(Splash.ID, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
    }

    public float getX() {
        return shape.getX();
    }

    public float getY() {
        return shape.getY();
    }

    public int getDirection() {
        return direction;
    }

    public int getHealth() {
        return health;
    }
}