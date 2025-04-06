package it.heron.hpet.modules.invisibilityintegration.vanilla;

import it.heron.hpet.modules.invisibilityintegration.InvisibilityIntegration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class BukkitInvisibility implements InvisibilityIntegration {
    @Override
    public boolean isInvisible(Entity entity) {
        if(!(entity instanceof LivingEntity)) return false;
        return ((LivingEntity)entity).isInvisible();
    }
}
