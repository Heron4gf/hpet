package it.heron.hpet.modules.pets.userpets.nametags;

import net.kyori.adventure.text.Component;
import org.bukkit.util.Vector;

public class NoNametag implements INametag {

    @Override
    public void setName(Component name) {

    }

    @Override
    public Component getName() {
        return Component.text("");
    }

    @Override
    public Vector relativeLocation() {
        return new Vector(0, 0, 0);
    }

    @Override
    public boolean isShown() {
        return false;
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }
}
