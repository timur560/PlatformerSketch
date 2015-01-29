package org.timur560.platformer.entities;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.ResourceLoader;

import org.timur560.platformer.Platformer;
import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.entities.weapon.Gun;
import org.timur560.platformer.entities.weapon.Weapon;
import org.timur560.platformer.world.Level;
import org.timur560.platformer.main.Game;

import java.net.URISyntaxException;

public class Player extends GameObject {
    public static int UP    = 1;
    public static int RIGHT = 2;
    public static int DOWN  = 3;
    public static int LEFT  = 4;

    private float gravity       = 0.7f;
    private float jumpStrength  = -12;
    private float speed         = 3;
    private float currentSpeed  = 0;
    private float inertion      = 0.9f;
    private float vX            = 0;
    private float vY            = 0;
    private int interations     = 5;
    private int direction       = RIGHT;
    private int jumpDelay       = 500;
    private long prevJumpTime   = 0;
    private boolean dead        = false;

    private Animation moveLeft, moveRight, stayLeft, stayRight, jumpRight, jumpLeft, moveLadder, stayLadder, current;
    private Weapon weapon;

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

    public void setWeapon(Weapon w) throws SlickException {
        weapon = w;
        w.init();
    }

    public void die() {
        dead = true;
    }

    public void update(GameContainer gc, int delta) throws SlickException {
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

        // Y acceleration
        vY += gravity;
        if (gc.getInput().isKeyDown(Input.KEY_Z)) { // jump
            shape.setY(shape.getY() + 0.5f);
            if ((level.collidesWith(shape) || level.collidesWithLadder(shape)) && System.currentTimeMillis() > jumpDelay + prevJumpTime) {
                vY = jumpStrength;
                prevJumpTime = System.currentTimeMillis();
            }

            if (level.collidesWithLadder(shape)) {
                current = stayLadder;
            }

            shape.setY(shape.getY() - 0.5f);
        }

        // Y Movement-Collisions
        float vYtemp = vY/interations;

        for (int i = 0; i < interations; i++) {
            shape.setY(shape.getY() + vYtemp);

            if (level.collidesWith(shape) || (level.collidesWithLadder(shape) && vYtemp > 0)) {
                shape.setY(shape.getY() - vYtemp);
                vY = 0;
            }
        }

        // X acceleration
        if (gc.getInput().isKeyDown(Input.KEY_LEFT)) {
            direction = LEFT;
            current = (vY == 0) ? moveLeft : jumpLeft;
            if (currentSpeed >= -speed) {
                currentSpeed -= inertion;
            }
        } else if (gc.getInput().isKeyDown(Input.KEY_RIGHT)) {
            direction = RIGHT;
            current = (vY == 0) ? moveRight : jumpRight;
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
        float vXtemp = vX/interations;
        for (int i = 0; i < interations; i++) {
            shape.setX( shape.getX() + vXtemp );
            if (level.collidesWith(shape)) {
                shape.setX( shape.getX() - vXtemp );
                vX = 0;
            }
        }

        // enemies collision
        if (level.collidesWithEnemie(shape)) {
            die();
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

    public float getX() {
        return shape.getX();
    }

    public float getY() {
        return shape.getY();
    }

    public Level getLevel() {
        return game.getLevel();
    }

    public int getDirection() {
        return direction;
    }

}