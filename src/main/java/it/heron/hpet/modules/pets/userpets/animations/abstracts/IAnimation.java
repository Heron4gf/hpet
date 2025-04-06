package it.heron.hpet.modules.pets.userpets.animations.abstracts;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public interface IAnimation {

    void nextStep();
    Vector relativeLocation(Location ownerLocation);
    String name();

}
