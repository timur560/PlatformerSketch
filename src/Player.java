import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

public class Player {
    public static int UP = 1;
    public static int RIGHT = 2;
    public static int DOWN = 3;
    public static int LEFT = 4;

    private float gravity = 0.7f;
    private float jumpStrength = -15;
    private float speed = 7;
    private static float currentSpeed = 0;
    private float inertion = 0.9f;
    private int interations = 5;
    private int direction = RIGHT;

    private Shape player;
    private Animation goLeft, goRight, stayLeft, stayRight, jumpRight, jumpLeft, current;
    private Level level;
    private Weapon weapon;
    private boolean dead = false;

    private float vX = 0;
    private float vY = 0;

    public Player(Level level) {
        this.level = level;
    }

    public void init(GameContainer gc) throws SlickException {
        player = new Rectangle(level.getEntryPoint()[0], level.getEntryPoint()[1], 45, 45);

        setWeapon(new Gun(this));

//        SpriteSheet sheet = new SpriteSheet(new Image(this.getClass().getResource("res/images/sprite.png").getFile()), 120, 130);
//        SpriteSheet sheet = new SpriteSheet(new Image(this.getClass().getResource("res/images/bt_sprite2.png").getFile()), 50, 50);
//        int[] animationSpeed = new int[5];
//        for (int i = 0; i <= 4; i++) animationSpeed[i] = 120;

//        goLeft = new Animation(sheet, new int[]{0,5,1,5,2,5,3,5,4,5,5,5,6,5,7,5,8,5,9,5}, animationSpeed);
//        goRight = new Animation(sheet, new int[]{0,7,1,7,2,7,3,7,4,7,5,7,6,7,7,7,8,7,9,7}, animationSpeed);
//        stayLeft = new Animation(sheet, new int[]{0,5}, new int[]{200});
//        stayRight = new Animation(sheet, new int[]{9,7}, new int[]{200});
//        jumpLeft = new Animation(sheet, new int[]{0,1}, new int[]{200});
//        jumpRight = new Animation(sheet, new int[]{0,3}, new int[]{200});

//        current = stayRight;
    }

    public void setWeapon(Weapon w) {
        weapon = w;
        w.init();
    }

    public void die() {
        dead = true;
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        if (dead) {
            player.setX(level.getEntryPoint()[0]);
            player.setY(level.getEntryPoint()[1]);
            dead = false;
            return;
        }

        // ladder collision
        if (gc.getInput().isKeyDown(Input.KEY_UP)) {
            direction = UP;
            if (level.collidesWithLadder(player)) {
                player.setY(player.getY() - speed);
            }
            if (level.collidesWith(player)) player.setY(player.getY() + speed);
        } else if (gc.getInput().isKeyDown(Input.KEY_DOWN)) {
            direction = DOWN;
            player.setY(player.getY() + speed);
            if (level.collidesWith(player)) player.setY(player.getY() - speed);
        }

        // Y acceleration
        vY += gravity;
        if (gc.getInput().isKeyDown(Input.KEY_Z)) {
            player.setY(player.getY() + 0.5f);
            if (level.collidesWith(player) || level.collidesWithLadder(player)) {
                vY = jumpStrength;
            }
            player.setY(player.getY() - 0.5f);
        }

        // Y Movement-Collisions
        float vYtemp = vY/interations;

        for (int i = 0; i < interations; i++) {
            player.setY(player.getY() + vYtemp);

            if (level.collidesWith(player) || (level.collidesWithLadder(player) && vYtemp > 0)) {
                player.setY(player.getY() - vYtemp);
                vY = 0;
            }
        }

        // X acceleration
        if (gc.getInput().isKeyDown(Input.KEY_LEFT)) {
            direction = LEFT;
            current = (vY == 0) ? goLeft : jumpLeft;
            if (currentSpeed >= -speed) {
                currentSpeed -= inertion;
            }
        } else if (gc.getInput().isKeyDown(Input.KEY_RIGHT)) {
            direction = RIGHT;
            current = (vY == 0) ? goRight : jumpRight;
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
            player.setX( player.getX() + vXtemp );
            if (level.collidesWith(player)) {
                player.setX( player.getX() - vXtemp );
                vX = 0;
            }
        }

        // enemies collision
        if (level.collidesWithEnemie(player)) {
            die();
        }

        // die if dropped in gap
        if (player.getY() > level.getHeight()) {
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
        // if (Platformer.DEBUG_MODE)
            g.draw(player);

        // current.draw(player.getX()-8, player.getY()-11);

        weapon.render(gc, g);
    }

    public float getX() {
        return player.getX();
    }

    public float getY() {
        return player.getY();
    }

    public Level getLevel() {
        return level;
    }

    public int getDirection() {
        return direction;
    }
}