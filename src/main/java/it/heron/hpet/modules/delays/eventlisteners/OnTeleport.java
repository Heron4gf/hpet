package it.heron.hpet.modules.delays.eventlisteners;

import it.heron.hpet.modules.delays.DelayModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OnTeleport extends DelayModule {

    /**
     * Constructs an OnTeleport event listener with the specified plugin instance.
     *
     * @param plugin the JavaPlugin instance to associate with this listener
     */
    public OnTeleport(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Returns the name identifier for this delay module.
     *
     * @return the string "teleport"
     */
    @Override
    public String name() {
        return "teleport";
    }

    @EventHandler
    void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        respawnFor(player);
    }
}
