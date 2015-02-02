package org.timur560.platformer.entities.weapon;

import org.timur560.platformer.core.Active;
import org.timur560.platformer.core.GameObject;
import org.timur560.platformer.entities.Player;
import org.timur560.platformer.main.Game;

/**
 * Created by qwer on 23.01.15.
 */
public abstract class Weapon extends GameObject {
    protected Active owner;
    protected int delay = 200;
    protected long prevAct = 0;

    public Weapon(Game g, Active o) {
        super(g);
        owner = o;
    }

    public final Active getOwner() {
        return owner;
    }

    public final void setOwner(Active o) {
        owner = o;
    }

    abstract public void act();
}
