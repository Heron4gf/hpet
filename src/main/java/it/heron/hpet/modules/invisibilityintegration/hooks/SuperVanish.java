package it.heron.hpet.modules.invisibilityintegration.hooks;

import de.myzelyam.api.vanish.VanishAPI;
import it.heron.hpet.modules.invisibilityintegration.InvisibilityIntegration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SuperVanish implements InvisibilityIntegration {

    @Override
    public boolean isInvisible(Entity entity) {
        if(!(entity instanceof Player)) return false;
        Player player = (Player) entity;
        return VanishAPI.isInvisible(player);
    }
}
