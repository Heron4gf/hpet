package it.heron.hpet.modules.abstracts;

import it.heron.hpet.main.PetPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class DefaultInstanceModule extends AbstractModule {
    /**
     * Constructs a DefaultInstanceModule with the specified JavaPlugin.
     *
     * @param plugin the JavaPlugin instance associated with this module
     */
    public DefaultInstanceModule(JavaPlugin plugin) {
        super(plugin);
    }
}
