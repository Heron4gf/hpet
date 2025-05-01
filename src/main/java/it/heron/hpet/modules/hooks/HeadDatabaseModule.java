package it.heron.hpet.modules.hooks;

import it.heron.hpet.modules.abstracts.PluginHook;
import org.bukkit.plugin.java.JavaPlugin;
import lombok.Getter;
import me.arcaniax.hdb.api.HeadDatabaseAPI;

public class HeadDatabaseModule extends PluginHook {

    @Getter
    private HeadDatabaseAPI headAPI;

    /**
     * Constructs a HeadDatabaseModule with the specified plugin instance.
     *
     * @param plugin the JavaPlugin instance associated with this module
     */
    public HeadDatabaseModule(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Returns the name identifier for this plugin hook.
     *
     * @return the string "HeadDatabase"
     */
    @Override
    public String name() {
        return "HeadDatabase";
    }

    @Override
    protected void onLoad() {
        this.headAPI = new HeadDatabaseAPI();
    }

    @Override
    protected void onUnload() {

    }
}
