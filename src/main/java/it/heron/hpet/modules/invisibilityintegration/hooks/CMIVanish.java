package it.heron.hpet.modules.invisibilityintegration.hooks;

import com.Zrips.CMI.CMI;
import it.heron.hpet.modules.invisibilityintegration.InvisibilityIntegration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CMIVanish implements InvisibilityIntegration {

    @Override
    public boolean isInvisible(Entity entity) {
        if(!(entity instanceof Player)) return false;
        Player player = (Player) entity;
        return CMI.getInstance().getPlayerManager().getUser(player).isVanished();
    }
}
