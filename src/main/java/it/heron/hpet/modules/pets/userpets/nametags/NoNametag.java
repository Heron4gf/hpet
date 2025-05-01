package it.heron.hpet.modules.pets.userpets.nametags;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;

public class NoNametag implements INametag {

    /**
     * Does nothing when called; this is a no-op implementation of setName.
     *
     * @param name the component to set as the name (ignored)
     */
    @Override
    public void setName(Component name) {
    }

    /**
     * Returns an empty text component as the nametag's name.
     *
     * @return an empty {@link Component}
     */
    @Override
    public Component getName() {
        return Component.text("");
    }

    /**
     * Does nothing when called, as this nametag implementation does not support teleportation.
     *
     * @param location the target location (ignored)
     */
    @Override
    public void teleport(Location location) {
    }

    /**
     * Indicates whether the nametag is currently shown.
     *
     * @return always {@code false}, as this implementation never displays a nametag
     */
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
