package it.heron.hpet.modules.invisibilityintegration.vanilla;

import it.heron.hpet.modules.invisibilityintegration.InvisibilityIntegration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SpigotVanish implements InvisibilityIntegration {
    @Override
    public boolean isInvisible(Entity entity) {
        if(!(entity instanceof Player)) return false;
        Player player = (Player) entity;
        return player.spigot().getHiddenPlayers().contains(player);
    }
}
