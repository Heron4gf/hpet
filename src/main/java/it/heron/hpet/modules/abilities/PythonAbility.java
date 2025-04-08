package it.heron.hpet.modules.abilities;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;
import it.heron.hpet.modules.abilities.abstracts.AbstractAbility;
import lombok.NonNull;
import org.bukkit.entity.Player;
import py4j.Py4JException;

import javax.annotation.Nullable; // Keep for enemy player
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Triggers execution of a specified function within Python scripts via Py4J.
 */
public class PythonAbility extends AbstractAbility {

    private static final File ABILITY_BASE_PATH = new File(PetPlugin.getInstance().getDataFolder(), "abilities");

    @NonNull private final List<File> scriptFiles;
    @NonNull private final String pythonFunctionName;
    @NonNull private final String pythonHandlerMethodName = "execute_script";

    // Transient state doesn't use constructor args
    private transient Method cachedPythonHandlerExecuteMethod = null;
    private transient long lastHandlerWarningTime = 0;
    private transient boolean pythonInvocationDisabled = false;

    // Constructor parameters assumed non-null from internal usage
    public PythonAbility(float seconds, @NonNull List<String> relativeScriptPaths, @NonNull String pythonFunctionName) {
        super(seconds);
        this.scriptFiles = findScriptFiles(relativeScriptPaths);
        this.pythonFunctionName = pythonFunctionName;
    }

    /** Locates and validates script files from relative paths. */
    @NonNull // This method should always return a list, even if empty
    private List<File> findScriptFiles(@NonNull List<String> relativePaths) {
        if (!ABILITY_BASE_PATH.exists() && !ABILITY_BASE_PATH.mkdirs()) {
            PetPlugin.getInstance().getLogger().warning("Failed to create Python abilities directory: " + ABILITY_BASE_PATH);
        }

        return relativePaths.stream()
                .filter(Objects::nonNull) // Filter out null paths just in case
                .map(scriptName -> {
                    if (!scriptName.toLowerCase().endsWith(".py")) scriptName += ".py";
                    return new File(ABILITY_BASE_PATH, scriptName);
                })
                .filter(file -> {
                    boolean exists = file.exists() && file.isFile();
                    if (!exists) {
                        PetPlugin.getInstance().getLogger().warning("Python ability script not found: " + file.getAbsolutePath());
                    }
                    return exists;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void onExecute(@NonNull UserPet userPet, @NonNull Player owner, boolean first_run, @Nullable Player enemy) {
        // Parameters from Bukkit events/internal logic assumed non-null except for 'enemy'
        if (pythonInvocationDisabled) return;

        getValidPythonHandler().ifPresent(handler -> {
            Map<String, Object> contextData = buildContextData(userPet, owner, first_run, enemy);
            scriptFiles.forEach(scriptFile -> { // scriptFiles list is guaranteed non-null
                if (!pythonInvocationDisabled) {
                    invokePythonHandler(handler, scriptFile, contextData);
                }
            });
        });
    }

    /** Retrieves the Python handler object if the bridge is ready and handler is registered. */
    @NonNull // Returns Optional, which is never null itself
    private Optional<Object> getValidPythonHandler() {
        AbilitiesHandler handler = (AbilitiesHandler) PetPlugin.getInstance().getModulesHandler().moduleByName("abilities");
        PythonBridge bridge = handler.getBridge();
        if (bridge == null || !bridge.isEnabled()) {
            return Optional.empty();
        }
        Object pythonHandler = bridge.getPythonExecutionHandler();
        if (pythonHandler == null) {
            logHandlerWarningPeriodically();
            return Optional.empty();
        }
        return Optional.of(pythonHandler);
    }

    /** Builds the context map to pass to the Python script. */
    @NonNull // Method always returns a map
    private Map<String, Object> buildContextData(@NonNull UserPet pet, @NonNull Player owner, boolean firstRun, @Nullable Player enemy) {
        Map<String, Object> context = new HashMap<>();
        context.put("owner_uuid", owner.getUniqueId().toString());
        context.put("owner_name", owner.getName());
        context.put("pet_id", pet.getId()+"");
        context.put("pet_type", pet.getPetType().getName());
        context.put("is_first_run", firstRun);
        context.put("enemy_uuid", (enemy != null) ? enemy.getUniqueId().toString() : null);
        context.put("enemy_name", (enemy != null) ? enemy.getName() : null);
        return context;
    }

    /** Invokes the execute method on the Python handler object using reflection. */
    private void invokePythonHandler(@NonNull Object pythonHandler, @NonNull File scriptFile, @NonNull Map<String, Object> contextData) {
        // Internal parameters assumed non-null based on calling context
        try {
            Method executeMethod = getCachedExecuteMethod(pythonHandler);
            executeMethod.invoke(pythonHandler, scriptFile.getAbsolutePath(), this.pythonFunctionName, contextData);
        } catch (NoSuchMethodException e) {
            PetPlugin.getInstance().getLogger().log(Level.SEVERE, "Python handler missing '" + this.pythonHandlerMethodName + "'. Disabling Python calls.", e);
            this.cachedPythonHandlerExecuteMethod = null;
            this.pythonInvocationDisabled = true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            logPythonInvocationError(scriptFile, e);
        } catch (Py4JException e) {
            PetPlugin.getInstance().getLogger().log(Level.WARNING, "Py4J error for " + scriptFile.getName() + ": " + e.getMessage());
        } catch (Exception e) {
            PetPlugin.getInstance().getLogger().log(Level.SEVERE, "Unexpected Java error invoking Python handler for: " + scriptFile.getName(), e);
        }
    }

    /** Gets or caches the Method object for the Python handler's execution method. */
    @NonNull // This method throws if it can't get the method, so return is non-null on success
    private Method getCachedExecuteMethod(@NonNull Object pythonHandler) throws NoSuchMethodException {
        if (this.cachedPythonHandlerExecuteMethod == null) {
            this.cachedPythonHandlerExecuteMethod = pythonHandler.getClass().getMethod(
                    this.pythonHandlerMethodName,
                    String.class, String.class, Map.class
            );
        }
        return this.cachedPythonHandlerExecuteMethod;
    }

    /** Logs errors that occur during the invocation of the Python handler method. */
    private void logPythonInvocationError(@NonNull File scriptFile, @NonNull Exception e) {
        PetPlugin.getInstance().getLogger().log(Level.SEVERE, "Error invoking Python method for script: " + scriptFile.getName(), e);
        if (e instanceof InvocationTargetException && e.getCause() != null) {
            PetPlugin.getInstance().getLogger().severe("Python Traceback: " + e.getCause().getMessage());
        }
    }

    /** Logs a warning about the missing Python handler, but only periodically. */
    private void logHandlerWarningPeriodically() {
        long now = System.currentTimeMillis();
        if (now - lastHandlerWarningTime > 10000) {
            PetPlugin.getInstance().getLogger().warning("Python execution handler not registered.");
            lastHandlerWarningTime = now;
        }
    }
}