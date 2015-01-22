import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

public class Player {

    private float gravity = 0.7f;
    private float jumpStrength = -15;
    private float speed = 7;
    private static float currentSpeed = 0;
    private float inertion = 0.9f;

    private int interations = 5;

    private Shape player;
    private Animation goLeft, goRight, stayLeft, stayRight, jumpRight, jumpLeft, current;
    private Level level;

    private float vX = 0;
    private float vY = 0;

    public Player(Level level) {
        this.level = level;
    }

    public void init(GameContainer gc) throws SlickException {
        // SpriteSheet sheet = new SpriteSheet(new Image(this.getClass().getResource("res/images/sprite.png").getFile()), 120, 130);
//        SpriteSheet sheet = new SpriteSheet(new Image(this.getClass().getResource("res/images/bt_sprite2.png").getFile()), 50, 50);
        player = new Rectangle(100, 100, 45, 45); // 120, 130);
//        int[] animationSpeed = new int[5];
//        for (int i = 0; i <= 4; i++) animationSpeed[i] = 120;

//        goLeft = new Animation(sheet, new int[]{0,5,1,5,2,5,3,5,4,5,5,5,6,5,7,5,8,5,9,5}, animationSpeed);
//        goRight = new Animation(sheet, new int[]{0,7,1,7,2,7,3,7,4,7,5,7,6,7,7,7,8,7,9,7}, animationSpeed);
//        stayLeft = new Animation(sheet, new int[]{0,5}, new int[]{200});
//        stayRight = new Animation(sheet, new int[]{9,7}, new int[]{200});
//        jumpLeft = new Animation(sheet, new int[]{0,1}, new int[]{200});
//        jumpRight = new Animation(sheet, new int[]{0,3}, new int[]{200});

        current = stayRight;
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.setColor(Color.red);
        // if (Platformer.DEBUG_MODE)
            g.draw(player);
        // current.draw(player.getX()-8, player.getY()-11);
    }

    public void update(GameContainer gc, int delta) throws SlickException {

        // ladder collision
        if (gc.getInput().isKeyDown(Input.KEY_UP)) {
            if (level.collidesWithLadder(player)) {
                player.setY(player.getY() - speed);
            }
            if (level.collidesWith(player)) player.setY(player.getY() + speed);
        } else if (gc.getInput().isKeyDown(Input.KEY_DOWN)) {
            player.setY(player.getY() + speed);
            if (level.collidesWith(player)) player.setY(player.getY() - speed);
        }

        // Y acceleration
        vY += gravity;
        if (gc.getInput().isKeyDown(Input.KEY_TAB)) {
            player.setY(player.getY() + 0.5f);
            if (level.collidesWith(player) || level.collidesWithLadder(player)) {
                vY = jumpStrength;
            }
            player.setY(player.getY() - 0.5f);
        }

        // Y Movement-Collisions
        float vYtemp = vY/interations;

        // if ((!level.collidesWithLadder(player) && vYtemp > 0) || vYtemp < 0) {

            for (int i = 0; i < interations; i++) {
                player.setY(player.getY() + vYtemp);

                if (level.collidesWith(player) || level.collidesWithLadder(player)) {
                    player.setY(player.getY() - vYtemp);
                    vY = 0;
                }
            }

        // }

        // X acceleration
        if (gc.getInput().isKeyDown(Input.KEY_LEFT)) {
            current = (vY == 0) ? goLeft : jumpLeft;
            if (currentSpeed >= -speed) {
                currentSpeed -= inertion;
            }
        } else if (gc.getInput().isKeyDown(Input.KEY_RIGHT)) {
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

        // set level horizontal offset
//        vXtemp = vX;
//
//        if (level.getOffsetLeft() > level.WIDTH - Platformer.WIDTH) {
//            level.setOffsetLeft(level.WIDTH - Platformer.WIDTH);
//        }
//
//        if (player.getX() > Platformer.WIDTH - 300 && level.getOffsetLeft() < level.WIDTH - Platformer.WIDTH) {
//            player.setX(player.getX() - vXtemp);
//            level.setOffsetLeft(level.getOffsetLeft() + vXtemp);
//            vX = 0;
//        } else if (player.getX() < 300 && level.getOffsetLeft() > 0) {
//            player.setX(player.getX() - vXtemp);
//            if (level.getOffsetLeft() + vXtemp >= 0) level.setOffsetLeft(level.getOffsetLeft() + vXtemp);
//            if (level.getOffsetLeft() < Math.abs(vXtemp)) level.setOffsetLeft(0);
//            vX = 0;
//        }

    }

    public float getX() {
        return player.getX();
    }

    public float getY() {
        return player.getY();
    }
}