package it.heron.hpet.itemsaddersupport;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import dev.lone.itemsadder.api.Events.ItemsAdderPackCompressedEvent;
import it.heron.hpet.Pet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderListener implements Listener {
    @EventHandler
    void onItemsAdderCompress(ItemsAdderPackCompressedEvent event) {
        Pet.getInstance().reloadConfig();
    }

    @EventHandler
    void onItemsAdderLoad(ItemsAdderLoadDataEvent event) {
        Pet.getInstance().reloadConfig();
    }
}
