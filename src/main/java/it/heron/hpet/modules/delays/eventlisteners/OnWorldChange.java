package it.heron.hpet.modules.delays.eventlisteners;

import it.heron.hpet.modules.delays.DelayModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OnWorldChange extends DelayModule {
    /**
     * Constructs an OnWorldChange event listener with the specified plugin instance.
     *
     * @param plugin the JavaPlugin instance to associate with this listener
     */
    public OnWorldChange(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Returns the name identifier for this module.
     *
     * @return the string "changeWorld"
     */
    @Override
    public String name() {
        return "changeWorld";
    }

    @EventHandler
    void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        respawnFor(player);
    }
}
