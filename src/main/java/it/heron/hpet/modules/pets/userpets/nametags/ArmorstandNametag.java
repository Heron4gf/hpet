package it.heron.hpet.modules.pets.userpets.nametags;

import it.heron.hpet.modules.pets.userpets.fakeentities.FakeArmostand;
import net.kyori.adventure.text.Component;

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
    public boolean isShown() {
        return fakeArmorstand != null && fakeArmostand.isSpawned();
    }
}
