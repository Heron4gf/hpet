package it.heron.hpet.modules.delays.eventlisteners;

import it.heron.hpet.modules.delays.DelayModule;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class OnTridentThrow extends DelayModule {

    @Override
    public String name() {
        return "tridentThrow";
    }

    @EventHandler
    void onTridentThrow(ProjectileLaunchEvent event) {
        if(!(event.getEntity() instanceof Trident)) {
            return;
        }
        if(!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity().getShooter();
        respawnFor(player);
    }
}
