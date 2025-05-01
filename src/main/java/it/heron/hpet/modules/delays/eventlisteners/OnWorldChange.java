package it.heron.hpet.modules.delays.eventlisteners;

import it.heron.hpet.modules.delays.DelayModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OnWorldChange extends DelayModule {
    public OnWorldChange(JavaPlugin plugin) {
        super(plugin);
    }

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
