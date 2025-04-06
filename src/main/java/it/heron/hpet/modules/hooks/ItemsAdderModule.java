package it.heron.hpet.modules.hooks;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import dev.lone.itemsadder.api.Events.ItemsAdderPackCompressedEvent;
import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.abstracts.PluginHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderModule extends PluginHook implements Listener {

    @Override
    public String name() {
        return "ItemsAdder";
    }

    @EventHandler
    void onItemsAdderCompress(ItemsAdderPackCompressedEvent event) {
        PetPlugin.getInstance().reload();
    }

    @EventHandler
    void onItemsAdderLoad(ItemsAdderLoadDataEvent event) {
        PetPlugin.getInstance().reload();
    }

    @Override
    protected void onLoad() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected void onUnload() {

    }
}
