// File: it/heron/hpet/modules/abilities/AbilitiesHandler.java
package it.heron.hpet.modules.abilities;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.abilities.abstracts.Ability;
import it.heron.hpet.modules.abstracts.DefaultInstanceModule;
import it.heron.hpet.modules.exceptions.InvalidUnloadException;
import lombok.Getter;
import lombok.NonNull; // Import if needed for parameter validation

import java.util.List; // Import List

/**
 * Module handler for abilities, manages the Python bridge lifecycle
 * and provides factory methods for creating abilities.
 */
public class AbilitiesHandler extends DefaultInstanceModule {

    @Getter // Keep getter if bridge instance needs to be accessed elsewhere
    private PythonBridge bridge = null; // Initialize in onLoad for clarity

    // Default function name to be called within Python ability scripts
    private static final String DEFAULT_PYTHON_FUNCTION_NAME = "on_execute";

    @Override
    public String name() {
        return "abilities";
    }

    @Override
    protected void onLoad() {
        // Initialize the bridge here when the module loads
        // This ensures PetPlugin.getInstance() and PetPlugin.getApi() are ready
        this.bridge = new PythonBridge(PetPlugin.getInstance(), PetPlugin.getApi());
        this.bridge.load();
    }

    @Override
    protected void onUnload() {
        // Ensure bridge exists before trying to close it
        if (this.bridge != null) {
            try {
                this.bridge.close();
            } catch (Exception e) {
                // Log the exception instead of throwing a generic one
                PetPlugin.getInstance().getLogger().severe("Error closing PythonBridge: " + e.getMessage());
                // Optionally rethrow if needed, but logging might be sufficient
                // throw new RuntimeException("Failed to properly unload PythonBridge", e);
            } finally {
                this.bridge = null; // Clear reference
            }
        } else {
            PetPlugin.getInstance().getLogger().warning("Attempted to unload AbilitiesHandler, but PythonBridge was not initialized.");
        }
    }

    /**
     * Static factory method to create a PythonAbility instance.
     * Uses the default Python function name "on_execute".
     *
     * @param relativeScriptPaths List of script filenames relative to the 'abilities' folder. Should not be null.
     * @param executionIntervalSeconds The interval in seconds at which the ability should run.
     * @return A configured PythonAbility instance implementing the Ability interface.
     */
    @NonNull
    public static Ability createPythonAbility(@NonNull List<String> relativeScriptPaths, float executionIntervalSeconds) {

        // Create and return the PythonAbility using the default function name
        return new PythonAbility(
                executionIntervalSeconds,
                relativeScriptPaths,
                DEFAULT_PYTHON_FUNCTION_NAME
        );
    }
}