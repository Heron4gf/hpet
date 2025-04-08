package it.heron.hpet.modules.abilities;

import it.heron.hpet.api.PetAPI;
import lombok.Getter;
import lombok.NonNull; // Import Lombok's NonNull
import org.bukkit.plugin.java.JavaPlugin;
import py4j.GatewayServer;
import javax.annotation.Nullable;
import java.util.logging.Level;

/**
 * Manages the Py4J GatewayServer lifecycle, connection state,
 * and holds the registered Python execution handler object.
 */
public class PythonBridge implements AutoCloseable {

    // Make final fields non-null where applicable
    @NonNull private final JavaPlugin plugin;
    @NonNull private final GatewayServer gatewayServer;
    @NonNull private final PythonEntryPoint entryPoint;

    private Object pythonExecutionHandler = null;
    @Getter
    private boolean enabled = false;

    // Constructor parameters marked NonNull
    public PythonBridge(@NonNull JavaPlugin plugin, @NonNull PetAPI api) {
        this.plugin = plugin; // Lombok doesn't auto-generate checks for manually assigned fields here
        // but the annotation serves as intent.
        // If we used @RequiredArgsConstructor, it would add checks.
        this.entryPoint = new PythonEntryPoint(plugin, api, this);
        this.gatewayServer = new GatewayServer(this.entryPoint);
    }

    /** Called internally by PythonEntryPoint upon Python handler registration. */
    void registerHandler(Object handler) { // handler can be null to clear
        this.pythonExecutionHandler = handler;
        if(handler != null) {
            plugin.getLogger().info("Python execution handler registered.");
        } else {
            plugin.getLogger().info("Python execution handler deregistered.");
        }
    }

    /** Retrieves the registered Python handler object. Null if not registered. */
    @Nullable // Explicitly mark methods that CAN return null
    public Object getPythonExecutionHandler() {
        return pythonExecutionHandler;
    }

    /** Starts the Py4J GatewayServer if not already running. */
    public void load() {
        if (enabled) return;
        try {
            gatewayServer.start();
            enabled = true;
            plugin.getLogger().info("Py4J GatewayServer started.");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to start Py4J GatewayServer", e);
            enabled = false;
        }
    }

    /** Shuts down the Py4J GatewayServer and clears the handler. */
    @Override
    public void close() {
        if (!enabled) return;
        try {
            gatewayServer.shutdown();
            plugin.getLogger().info("Py4J GatewayServer shut down.");
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error during Py4J GatewayServer shutdown", e);
        } finally {
            enabled = false;
            pythonExecutionHandler = null;
        }
    }
}