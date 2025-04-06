package it.heron.hpet.modules.abstracts;

import it.heron.hpet.main.PetPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class DefaultInstanceModule extends AbstractModule {
    private static final JavaPlugin PLUGIN = PetPlugin.getInstance();

    public DefaultInstanceModule() {
        super(PLUGIN);
    }
}
