package it.heron.hpet.modules.invisibilityintegration.hooks;

import com.earth2me.essentials.Essentials;
import it.heron.hpet.modules.invisibilityintegration.InvisibilityIntegration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EssentialsVanish implements InvisibilityIntegration {

    private Essentials essentials = (Essentials) Essentials.getProvidingPlugin(Essentials.class);

    @Override
    public boolean isInvisible(Entity entity) {
        if(!(entity instanceof Player)) return false;
        Player player = (Player) entity;
        return essentials.getUser(player).isVanished();
    }
}
