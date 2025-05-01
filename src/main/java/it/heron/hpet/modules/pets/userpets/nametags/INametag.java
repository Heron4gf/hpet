package it.heron.hpet.modules.pets.userpets.nametags;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public interface INametag {

    /****
 * Sets the nametag's display name to the specified component.
 *
 * @param name the new name to assign to the nametag
 */
void setName(Component name);
    /**
 * Retrieves the current name of the nametag as a Component.
 *
 * @return the nametag's name
 */
Component getName();

    /**
 * Moves the nametag to the specified location.
 *
 * @param location the target location to teleport the nametag to
 */
void teleport(Location location);

    /**
 * Returns whether the nametag is currently visible.
 *
 * @return true if the nametag is shown, false otherwise
 */
boolean isShown();
    /****
 * Makes the nametag visible.
 */
void show();
    void hide();
}
