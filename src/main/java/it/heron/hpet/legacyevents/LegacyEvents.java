package it.heron.hpet.legacyevents;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class LegacyEvents implements Listener {
    @EventHandler
    void onEntitySpawn(EntitySpawnEvent event) {
        if(event.getEntity().getCustomName() != null && event.getEntity().getCustomName().equals("hpet")) {
            event.setCancelled(false);
        }
    }
    @EventHandler
    void onRightClick(EntityInteractEvent event) {
        if(event.getEntity().getCustomName() != null && event.getEntity().getCustomName().equals("hpet")) {
            event.setCancelled(true);
        }
    }

}
