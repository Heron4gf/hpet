package it.heron.hpet.modules.pets.userpets.nametags;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;

public class NoNametag implements INametag {

    @Override
    public void setName(Component name) {
    }

    @Override
    public Component getName() {
        return Component.text("");
    }

    @Override
    public void teleport(Location location) {
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
