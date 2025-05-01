package it.heron.hpet.modules.delays.eventlisteners;

import it.heron.hpet.modules.delays.DelayModule;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OnTridentThrow extends DelayModule {

    /**
     * Constructs an OnTridentThrow event listener with the specified plugin instance.
     *
     * @param plugin the JavaPlugin instance associated with this module
     */
    public OnTridentThrow(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Returns the name identifier for this delay module.
     *
     * @return the string "tridentThrow"
     */
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
