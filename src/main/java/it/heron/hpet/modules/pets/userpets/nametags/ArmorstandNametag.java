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

    @Override
    public Component getName() {
        return fakeArmorstand.getName();
    }

    @Override
    public void teleport(Location location) {
        fakeArmorstand.teleport(location.clone().add(0,-1,0), false);
    }

    @Override
    public boolean isShown() {
        return fakeArmorstand != null && fakeArmorstand.isSpawned();
    }
}
