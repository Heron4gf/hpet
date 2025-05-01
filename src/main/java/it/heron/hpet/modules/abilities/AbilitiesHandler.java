// File: it/heron/hpet/modules/abilities/AbilitiesHandler.java
package it.heron.hpet.modules.abilities;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.abilities.abstracts.Ability;
import it.heron.hpet.modules.abstracts.DefaultInstanceModule;
import it.heron.hpet.modules.exceptions.InvalidUnloadException;
import lombok.Getter;
import lombok.NonNull; // Import if needed for parameter validation
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List; // Import List

/**
 * Module handler for abilities, manages the Python bridge lifecycle
 * and provides factory methods for creating abilities.
 */
public class AbilitiesHandler extends DefaultInstanceModule {

    @Getter // Keep getter if bridge instance needs to be accessed elsewhere

    // Default function name to be called within Python ability scripts
    private static final String DEFAULT_PYTHON_FUNCTION_NAME = "on_execute";

    public AbilitiesHandler(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String name() {
        return "abilities";
    }

    @Override
    protected void onLoad() {
    }

    @Override
    protected void onUnload() {

    }


}