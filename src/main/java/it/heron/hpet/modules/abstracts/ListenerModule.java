package it.heron.hpet.modules.abstracts;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ListenerModule extends DefaultInstanceModule implements Listener {

    public ListenerModule(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected void onUnload() {

    }
}
