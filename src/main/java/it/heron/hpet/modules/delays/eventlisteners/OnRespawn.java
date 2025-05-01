package it.heron.hpet.modules.delays.eventlisteners;

import it.heron.hpet.modules.delays.DelayModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OnRespawn extends DelayModule {
    /**
     * Constructs an OnRespawn event listener for handling player respawn events.
     *
     * @param plugin the JavaPlugin instance associated with this module
     */
    public OnRespawn(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Returns the identifier name for this delay module.
     *
     * @return the string "respawn"
     */
    @Override
    public String name() {
        return "respawn";
    }

    @EventHandler
    void onRespawn(PlayerRespawnEvent event) {
        respawnFor(event.getPlayer());
    }
}
