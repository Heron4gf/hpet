package it.heron.hpet.animation;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Particle;

public @Data
class PetParticle {

    private Particle particle;

    public PetParticle(Particle particle) {
        this.particle = particle;
    }

    public void tick(Location loc) {
        //if(this.particle == null) return;
        loc.getWorld().spawnParticle(this.particle, loc, 1);
    }

}

