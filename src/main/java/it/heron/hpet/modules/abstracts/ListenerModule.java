package it.heron.hpet.modules.abstracts;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class ListenerModule extends DefaultInstanceModule implements Listener {

    @Override
    protected void onLoad() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected void onUnload() {

    }
}
