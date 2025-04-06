package it.heron.hpet.modules.delays.eventlisteners;

import it.heron.hpet.modules.delays.DelayModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

public class OnRespawn extends DelayModule {
    @Override
    public String name() {
        return "respawn";
    }

    @EventHandler
    void onRespawn(PlayerRespawnEvent event) {
        respawnFor(event.getPlayer());
    }
}
