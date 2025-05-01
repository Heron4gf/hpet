package it.heron.hpet.modules.delays.eventlisteners;

import it.heron.hpet.modules.delays.DelayModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OnTeleport extends DelayModule {

    public OnTeleport(JavaPlugin plugin) {
        super(plugin);
    }

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
