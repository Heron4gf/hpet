package it.heron.hpet.modules.abstracts;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ListenerModule extends DefaultInstanceModule implements Listener {

    /**
     * Constructs a ListenerModule with the specified JavaPlugin instance.
     *
     * @param plugin the JavaPlugin associated with this module
     */
    public ListenerModule(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Registers this module as an event listener with the Bukkit plugin manager during the load phase.
     */
    @Override
    protected void onLoad() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected void onUnload() {

    }
}
