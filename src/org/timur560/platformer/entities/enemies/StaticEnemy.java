package org.timur560.platformer.entities.enemies;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.timur560.platformer.entities.weapon.Weapon;
import org.timur560.platformer.main.Game;

import java.util.List;

/**
 * Created by qwer on 23.01.15.
 */
public class StaticEnemy extends Enemy {

    public StaticEnemy(Game g, List<Long> pos, List<Double> r, String t, boolean cd, int d) {
        super(g, pos, r, t, cd, d);
    }

    @Override
    public void init() throws SlickException {

    }
}
