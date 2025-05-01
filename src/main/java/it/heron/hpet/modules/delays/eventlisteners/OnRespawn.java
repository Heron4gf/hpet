package it.heron.hpet.modules.delays.eventlisteners;

import it.heron.hpet.modules.delays.DelayModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OnRespawn extends DelayModule {
    public OnRespawn(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String name() {
        return "respawn";
    }

    @EventHandler
    void onRespawn(PlayerRespawnEvent event) {
        respawnFor(event.getPlayer());
    }
}
