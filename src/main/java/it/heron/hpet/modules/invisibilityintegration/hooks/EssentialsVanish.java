package it.heron.hpet.modules.invisibilityintegration.hooks;

import com.earth2me.essentials.Essentials;
import it.heron.hpet.modules.invisibilityintegration.InvisibilityIntegration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.Optional;

public class EssentialsVanish implements InvisibilityIntegration {

    private Essentials essentials;

    /**
     * Retrieves and caches the Essentials plugin instance if available and enabled.
     *
     * @return an {@code Optional} containing the Essentials instance, or empty if the plugin is not present or not enabled
     */
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

    /**
     * Determines whether the given entity is a vanished player according to the Essentials plugin.
     *
     * @param entity the entity to check for invisibility
     * @return {@code true} if the entity is a player and is marked as vanished by Essentials; {@code false} otherwise
     */
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
