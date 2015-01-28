package org.timur560.platformer.entities.weapon;

import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.entities.Player;
import org.timur560.platformer.main.Game;

/**
 * Created by qwer on 23.01.15.
 */
public abstract class Weapon extends GameObject {
    protected Player player;

    protected int delay = 300;
    protected long prevAct = 0;

    public Weapon(Game g, Player p) {
        super(g);
        player = p;
    }

    abstract public void act();
}
