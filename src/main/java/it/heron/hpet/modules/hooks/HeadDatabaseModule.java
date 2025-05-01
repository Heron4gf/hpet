package it.heron.hpet.modules.hooks;

import it.heron.hpet.modules.abstracts.PluginHook;
import org.bukkit.plugin.java.JavaPlugin;
import lombok.Getter;
import me.arcaniax.hdb.api.HeadDatabaseAPI;

public class HeadDatabaseModule extends PluginHook {

    @Getter
    private HeadDatabaseAPI headAPI;

    public HeadDatabaseModule(JavaPlugin plugin) {
        super(plugin);
    }

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
