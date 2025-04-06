package it.heron.hpet.modules.pets.userpets.animations;

import it.heron.hpet.modules.pets.userpets.animations.abstracts.IAnimation;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class NoAnimation implements IAnimation {
    @Override
    public void nextStep() {

    }

    @Override
    public Vector relativeLocation(Location ownerLocation) {
        return ownerLocation.toVector();
    }

    @Override
    public String name() {
        return "none";
    }
}
