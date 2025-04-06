package it.heron.hpet.modules.invisibilityintegration.vanilla;

import it.heron.hpet.modules.invisibilityintegration.InvisibilityIntegration;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SpectatorInvisibility implements InvisibilityIntegration {
    @Override
    public boolean isInvisible(Entity entity) {
        if(!(entity instanceof Player)) return false;
        Player player = (Player) entity;
        return player.getGameMode() == GameMode.SPECTATOR;
    }
}
