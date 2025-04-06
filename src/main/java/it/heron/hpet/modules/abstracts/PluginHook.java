package it.heron.hpet.modules.abstracts;

import it.heron.hpet.modules.exceptions.RefusedLoadException;
import org.bukkit.Bukkit;

public abstract class PluginHook extends DefaultInstanceModule {

    @Override
    public void load() {
        if(loaded) return;
        if(!canHook()) throw new RefusedLoadException();
        onLoad();
        loaded = true;
    }

    protected boolean canHook() {
        return canHook(name());
    }

    protected boolean canHook(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null && Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }
}
