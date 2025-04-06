package it.heron.hpet.modules.delays.eventlisteners;

import it.heron.hpet.modules.delays.DelayModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class OnWorldChange extends DelayModule {
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
