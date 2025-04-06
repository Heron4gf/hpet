package it.heron.hpet.modules.abstracts;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractModule implements Module {

    @Getter
    protected boolean loaded = false;

    protected JavaPlugin plugin;

    public AbstractModule(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        if(loaded) return;
        onLoad();
        loaded = true;
    }

    @Override
    public void unload() {
        if(!loaded) return;
        onUnload();
        loaded = false;
    }


    protected abstract void onLoad();
    protected abstract void onUnload();
}
