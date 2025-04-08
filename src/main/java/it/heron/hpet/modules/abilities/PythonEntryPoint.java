package it.heron.hpet.modules.abilities;

import it.heron.hpet.api.PetAPI;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import javax.annotation.Nullable; // Standard Nullable annotation

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Provides thread-safe methods for Python scripts to interact with Bukkit/HPET APIs via Py4J.
 * Acts as the sole entry point exposed to the Py4J Gateway.
 */
@RequiredArgsConstructor // Generates constructor for final fields, including @NonNull checks
public class PythonEntryPoint {

    @NonNull private final JavaPlugin plugin;
    @NonNull private final PetAPI petApi;
    @NonNull private final PythonBridge bridge;

    // --- Method Called BY Python Client ---

    public void registerPythonExecutionHandler(Object handler) { // handler from Python could be null
        if (handler != null) {
            this.bridge.registerHandler(handler);
        } else {
            logWarning("Python attempted to register a null execution handler.");
        }
    }

    // --- Thread Safe Bukkit/API Wrappers Called BY Python ---

    // Use @NonNull on parameters received from Python where null is invalid
    public void logInfo(@NonNull String message) {
        plugin.getLogger().info(message);
    }

    public void logWarning(@NonNull String message) {
        plugin.getLogger().warning(message);
    }

    @Nullable // Method CAN return null if player not found
    public Player getPlayerByUUID(@NonNull String uuidString) { // Input string shouldn't be null
        return supplyOnMainThread(() -> {
            try {
                return Bukkit.getPlayer(UUID.fromString(uuidString));
            } catch (IllegalArgumentException e) {
                logWarning("Invalid UUID format: " + uuidString);
                return null;
            }
        }).join();
    }

    @Nullable // Method CAN return null
    public Player getPlayerByName(@NonNull String name) { // Input name shouldn't be null
        return supplyOnMainThread(() -> Bukkit.getPlayerExact(name)).join();
    }

    public void sendMessageToPlayer(@NonNull String playerUuidString, @NonNull String message) {
        runOnMainThread(() -> {
            Player player = getPlayerByUUID(playerUuidString);
            if (isPlayerOnline(player)) {
                player.sendMessage(message);
            }
        });
    }

    public boolean applyPotionEffectToPlayer(@NonNull String playerUuidString, @NonNull String effectTypeName, int durationSeconds, int amplifier) {
        return supplyOnMainThread(() -> {
            Player player = getPlayerByUUID(playerUuidString);
            if (!isPlayerOnline(player)) return false;

            PotionEffectType effectType = PotionEffectType.getByName(effectTypeName.toUpperCase());
            if (effectType == null) {
                logWarning("Invalid PotionEffectType name: " + effectTypeName);
                return false;
            }
            PotionEffect effect = new PotionEffect(effectType, durationSeconds * 20, amplifier, false, true);
            return player.addPotionEffect(effect, true);
        }).join();
    }

    public boolean runConsoleCommand(@NonNull String command) {
        return supplyOnMainThread(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)).join();
    }

    // --- Private Helper Methods ---

    private boolean isPlayerOnline(@Nullable Player player) { // Input player can be null
        return player != null && player.isOnline();
    }

    // Annotate functional interface parameters if desired (mainly doc/static analysis)
    private <T> CompletableFuture<T> supplyOnMainThread(@NonNull Supplier<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();
        if (Bukkit.isPrimaryThread()) {
            executeSafely(supplier, future);
        } else {
            Bukkit.getScheduler().runTask(plugin, () -> executeSafely(supplier, future));
        }
        return future;
    }

    private void runOnMainThread(@NonNull Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            executeSafely(runnable);
        } else {
            Bukkit.getScheduler().runTask(plugin, () -> executeSafely(runnable));
        }
    }

    // Parameters for internal helpers assumed non-null by calling context
    private <T> void executeSafely(Supplier<T> supplier, CompletableFuture<T> future) {
        try {
            future.complete(supplier.get());
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, "Exception executing task on main thread", t);
            future.completeExceptionally(t);
        }
    }

    private void executeSafely(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, "Exception executing task on main thread", t);
        }
    }
}