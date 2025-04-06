package it.heron.hpet.modules.delays.eventlisteners;

import it.heron.hpet.modules.delays.DelayModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;

public class OnTeleport extends DelayModule {

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
