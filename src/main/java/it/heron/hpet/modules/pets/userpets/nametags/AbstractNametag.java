package it.heron.hpet.modules.pets.userpets.nametags;

import net.kyori.adventure.text.Component;
import org.bukkit.util.Vector;

public abstract class AbstractNametag implements INametag {

    public AbstractNametag(Component nametag) {
        // do some input validation and modify
    }

    @Override
    public Vector relativeLocation() {
        return new Vector( 0, 1, 0);
    }

    public void show() {
        if(isShown()) return;
    }

    public void hide() {
        if(!isShown()) return;
    }

}
