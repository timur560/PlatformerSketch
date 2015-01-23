import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import java.util.List;

/**
 * Created by qwer on 23.01.15.
 */
public class MovingEnemy extends Enemy {
    private List<List<Long>> path;
    private int posIndex = 0;

    private float t = 0, speed = 0.5f;

    public MovingEnemy(float[] vertices, List<List<Long>> path, Double speed) {
        super(vertices);
        this.path = path;
        this.speed = speed.floatValue();
    }

    public void update(GameContainer gc, int delta) throws SlickException {
        if (path.isEmpty()) return;

        List<Long> currentPos, prevPos;

        prevPos = path.get(posIndex);
        if (posIndex + 1 >= path.size()) currentPos = path.get(0);
        else currentPos = path.get(posIndex + 1);

        t += speed / delta;

        shape.setX((1 - t) * prevPos.get(0) + t * currentPos.get(0));
        shape.setY((1 - t) * prevPos.get(1) + t * currentPos.get(1));

        if (t > 1) {
            t = 0;
            if (posIndex + 1 >= path.size()) {
                posIndex = 0;
            } else {
                posIndex++;
            }
        }
    }

}
