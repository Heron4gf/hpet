package it.heron.hpet.modules.pets.userpets.nametags;

import it.heron.hpet.modules.pets.userpets.fakeentities.FakeArmostand;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

public class ArmorstandNametag extends AbstractNametag {
    private FakeArmostand fakeArmorstand = null;

    public ArmorstandNametag(Component nametag) {
        super(nametag);
        fakeArmorstand = new FakeArmostand(nametag, false, false);
    }

    @Override
    public void setName(Component name) {
        fakeArmorstand.setName(name);
    }

    /**
     * Retrieves the current name displayed by the internal fake armor stand.
     *
     * @return the nametag component currently set on the fake armor stand
     */
    @Override
    public Component getName() {
        return fakeArmorstand.getName();
    }

    /**
     * Teleports the nametag's armor stand to the specified location, offset by -1 on the Y-axis.
     *
     * @param location the target location to teleport to; the armor stand will appear one block below this position
     */
    @Override
    public void teleport(Location location) {
        fakeArmorstand.teleport(location.clone().add(0,-1,0), false);
    }

    /**
     * Determines whether the nametag's armor stand entity is currently spawned.
     *
     * @return true if the internal armor stand exists and is spawned; false otherwise
     */
    @Override
    public boolean isShown() {
        return fakeArmorstand != null && fakeArmorstand.isSpawned();
    }
}
