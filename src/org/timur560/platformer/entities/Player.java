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
import org.timur560.platformer.world.Ladder;
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
        float[] pos = game.getLevel().getZone().getPortals().get(0).getPos();
        shape = new Rectangle(pos[0], pos[1], 20, 42);
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
        Level level = game.getLevel();

        if (level.getTimeLeft() <= 0) die(); // :( ooops...

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

        // ladder collision
        if (gc.getInput().isKeyDown(Input.KEY_UP) || gc.getInput().isControllerUp(0)) {
            direction = UP;
            current = moveLadder;
            if (level.collidesWithLadder(shape) != null) {
                shape.setY(shape.getY() - speed);
            } else {
                current = stayRight;
            }
            if (level.collidesWith(shape)) {
                shape.setY(shape.getY() + speed);
            }
        } else if (gc.getInput().isKeyDown(Input.KEY_DOWN) || gc.getInput().isControllerDown(0)) {
            direction = DOWN;
            shape.setY(shape.getY() + speed);
            current = moveLadder;
            if (level.collidesWith(shape)) {
                shape.setY(shape.getY() - speed);
                current = stayRight;
            }
        } else if (level.collidesWithLadder(shape) != null) {
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
                    || (level.collidesWithLadder(shape) != null && vY > 0)) {

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
        if (gc.getInput().isKeyDown(Input.KEY_Z) || gc.getInput().isButton1Pressed(0)) { // jump
            shape.setY(shape.getY() + 0.5f);

            MovingBlock mb = level.collidesWithMovingBlock(shape);
            Ladder l = level.collidesWithLadder(shape);

            // if (l!= null) System.out.println(l.getShape().getY() + " " + (shape.getY() + shape.getHeight()));

            if (level.collidesWith(shape)
                    || (l != null && l.getShape().getY() >= shape.getY() - 0.5f + shape.getHeight())
                    || mb != null
                    || level.collidesWighMovingPlatform(shape) != null) {
                vY = jumpStrength;
            }

//            if (l != null && l.getShape().getY() < shape.getY() + shape.getHeight()) {
//                current = stayLadder;
//            }

            shape.setY(shape.getY() - 0.5f);
        }

        // X acceleration
        if (gc.getInput().isKeyDown(Input.KEY_LEFT) || gc.getInput().isControllerLeft(0)) {
            direction = LEFT;
            current = ((int) vY == 0) ? moveLeft : jumpLeft;
            if (currentSpeed >= -speed) {
                currentSpeed -= inertion;
            }
        } else if (gc.getInput().isKeyDown(Input.KEY_RIGHT) || gc.getInput().isControllerRight(0)) {
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
        if ((gc.getInput().isKeyDown(Input.KEY_C) || gc.getInput().isButtonPressed(1, 0))
                && (prevTeleport == 0 || System.currentTimeMillis() > prevTeleport + teleportDelay)) {
            for (Portal p : game.getLevel().getPortals()) {
                if (p.getPortalShape().intersects(shape)) {
                    if (p.getDest().get(0) == level.getId()) { // if current level portal
                        if (p.getDest().get(1) == level.getZone().getId()) { // if current zone portal
                            float[] pos = level.getPortals().get(p.getDest().get(2).intValue()).getPos();
                            teleport(shape.getX(), shape.getY(), pos[0] + 10, pos[1]);
                            break;
                        } else { // go to another zone in current level
                            level.goToZone(
                                    p.getDest().get(1).intValue(), // zone
                                    p.getDest().get(2).intValue()); // portal
                            float[] pos = level.getZone().getPortals().get(p.getDest().get(2).intValue()).getPos();
                            shape.setX(pos[0]);
                            shape.setY(pos[1]);
                        }
                    } else { // load another level
                        ((Platformer) game.game).setCurrentLevel(p.getDest().get(0).intValue());
                        game.game.enterState(Splash.ID, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
                    }
                }
            }
            prevTeleport = System.currentTimeMillis();
        }

        // enemies collision
        if (level.collidesWithEnemie(shape)) {
            decreaseHealth();
        }

        // die if dropped in gap
        if (shape.getY() > level.getHeight()) {
            die();
        }

        // shoot
        if (gc.getInput().isKeyDown(Input.KEY_X) || gc.getInput().isButtonPressed(2, 0)) {
            weapon.act();
        }

        weapon.update(gc, delta);
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.setColor(Color.blue);
        if (Platformer.DEBUG_MODE) g.draw(shape);
        g.drawAnimation(current, shape.getX() - 12, shape.getY() - 3);

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
            vY = -10; // jumpStrength;
            if (health <= 0) die();
            prevDecreaseHealh = System.currentTimeMillis();
        }
    }

    public void die() {
        dead = true;
        health = 10;
        System.out.println("You dead :(");
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