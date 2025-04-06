package it.heron.hpet.modules.invisibilityintegration.vanilla;

import it.heron.hpet.modules.invisibilityintegration.InvisibilityIntegration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionInvisibility implements InvisibilityIntegration {
    @Override
    public boolean isInvisible(Entity entity) {
        if(!(entity instanceof LivingEntity)) return false;
        LivingEntity livingEntity = (LivingEntity) entity;
        for(PotionEffect potionEffect : livingEntity.getActivePotionEffects()) {
            if(potionEffect.getType().equals(PotionEffectType.INVISIBILITY)) return true;
        }
        return false;
    }
}
