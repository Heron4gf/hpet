package it.heron.hpet.modules.invisibilityintegration.hooks;

import com.earth2me.essentials.Essentials;
import it.heron.hpet.modules.invisibilityintegration.InvisibilityIntegration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.Optional;

public class EssentialsVanish implements InvisibilityIntegration {

    private Essentials essentials;

    private Optional<Essentials> getEssentials() {
        try {
            if(essentials == null) {
                Plugin plugin = Essentials.getProvidingPlugin(Essentials.class);
                if(plugin != null && plugin.isEnabled()) {
                    this.essentials = (Essentials) plugin;
                }
            }
            return Optional.ofNullable(essentials);
        } catch (NoClassDefFoundError e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean isInvisible(Entity entity) {
        if(!(entity instanceof Player)) return false;
        
        Optional<Essentials> ess = getEssentials();
        if(!ess.isPresent()) return false;
        
        try {
            Player player = (Player) entity;
            return ess.get().getUser(player).isVanished();
        } catch (Exception e) {
            return false;
        }
    }
}
