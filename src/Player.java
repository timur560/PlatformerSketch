import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

public class Player {

    private static float gravity = 0.7f;
    private static float jumpStrength = -15;
    private static float speed = 10;
    private static float currentSpeed = 0;
    private static float inertion = 0.9f;

    private static int interations = 5;

    private Shape player;
    private Animation player1;
    private StaticLevel level;

    private float vX = 0;
    private float vY = 0;

    public Player(StaticLevel level) {
        this.level = level;
    }

    public void init(GameContainer gc) throws SlickException {
        SpriteSheet sheet = new SpriteSheet("/res/images/mario.png",40,40);
        player = new Rectangle(100, 100, 45, 45);
        player1 = new Animation(sheet, new int[]{0,2,1,2}, new int[]{200,200});
        player1.setAutoUpdate(true);
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.setColor(Color.red);
        g.draw(player);
        player1.draw(100, 100);
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        // Y acceleration
        vY += gravity;
        if (gc.getInput().isKeyDown(Input.KEY_UP)) {
            player.setY( player.getY()+0.5f );
            if (level.collidesWith(player)) {
                vY = jumpStrength;
            }
            player.setY( player.getY()-0.5f );
        }

        // Y Movement-Collisions
        float vYtemp = vY/interations;
        for (int i = 0; i < interations; i++) {
            player.setY( player.getY() + vYtemp );
            if (level.collidesWith(player)) {
                player.setY( player.getY() - vYtemp );
                vY = 0;
            }
        }

        // X acceleration
        if (gc.getInput().isKeyDown(Input.KEY_LEFT)) {
            if (currentSpeed >= -speed) {
                currentSpeed -= inertion;
            }
        } else if (gc.getInput().isKeyDown(Input.KEY_RIGHT)) {
            if (currentSpeed <= speed) {
                currentSpeed += inertion;
            }
        } else {
            if (currentSpeed < 0) {
                if (currentSpeed > -inertion) currentSpeed = 0;
                else currentSpeed += inertion;
            } else if (currentSpeed > 0) {
                if (currentSpeed < inertion) currentSpeed = 0;
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
        vXtemp = vX;

        if (level.getOffset() > level.WIDTH - Platformer.WIDTH) level.setOffset(level.WIDTH - Platformer.WIDTH);

        if (player.getX() > Platformer.WIDTH - 300 && level.getOffset() < level.WIDTH - Platformer.WIDTH) {
            player.setX(player.getX() - vXtemp);
            level.setOffset(level.getOffset() + vXtemp);
            vX = 0;
        } else if (player.getX() < 300 && level.getOffset() > 0) {
            player.setX(player.getX() - vXtemp);
            if (level.getOffset() + vXtemp >= 0) level.setOffset(level.getOffset() + vXtemp);
            if (level.getOffset() < Math.abs(vXtemp)) level.setOffset(0);
            vX = 0;
        }

    }
}