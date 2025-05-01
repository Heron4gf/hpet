package it.heron.hpet.modules;

import it.heron.hpet.modules.abilities.AbilitiesHandler;
import it.heron.hpet.modules.abstracts.Module;
import it.heron.hpet.modules.exceptions.InvalidLoadException;
import it.heron.hpet.modules.exceptions.RefusedLoadException;
import it.heron.hpet.modules.hooks.HeadDatabaseModule;
import it.heron.hpet.modules.hooks.ItemsAdderModule;
import it.heron.hpet.modules.hooks.PapiModule;
import it.heron.hpet.modules.hooks.VaultHook;
import it.heron.hpet.modules.invisibilityintegration.InvisibilityHandler;
import it.heron.hpet.modules.messages.MessagesHandler;
import it.heron.hpet.modules.pets.PetTypesHandler;
import it.heron.hpet.modules.pets.PetsHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import it.heron.hpet.main.PetPlugin;

import java.util.*;

public class ModulesHandler {

    private JavaPlugin plugin;
    private final HashMap<String, Module> modules = new HashMap<>();

    public ModulesHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks if a module with the specified name exists.
     *
     * @param moduleName the name of the module to check (case-insensitive)
     * @return true if the module exists, false otherwise
     */
    public boolean hasModule(String moduleName) {
        return this.modules.containsKey(moduleName);
    }

    /**
     * Retrieves a module by its name, ignoring case.
     *
     * @param moduleName the name of the module to retrieve
     * @return the corresponding Module instance, or null if not found
     */
    public Module moduleByName(String moduleName) {
        return this.modules.get(moduleName.toLowerCase());
    }

    /**
     * Loads and registers all valid modules, then initializes them.
     *
     * This method retrieves the list of valid modules, adds each to the internal module map,
     * and then loads all registered modules to make them active within the plugin environment.
     */
    public void loadModules() {
        for(Module module : validModules()) {
            addModule(module);
        }
        loadAddedModules();
    }

    /**
     * Unloads all currently loaded modules and removes them from the internal registry.
     */
    public void unloadModules() {
        new ArrayList<>(modules.values()).forEach(this::removeModule);
    }

    /**
     * Creates and returns a collection of all predefined module instances for the plugin.
     *
     * @return a collection containing new instances of all valid modules
     */
    private Collection<Module> validModules() {
        List<Module> modules = new ArrayList<>();
        modules.add(new DatabaseModule(plugin));
        modules.add(new PetTypesHandler(plugin));
        modules.add(new PetsHandler(plugin));
        modules.add(new AbilitiesHandler(plugin));

        modules.add(new PapiModule(plugin));
        modules.add(new VaultHook(plugin));
        modules.add(new ItemsAdderModule(plugin));
        modules.add(new HeadDatabaseModule(plugin));
        modules.add(new InvisibilityHandler(plugin));
        modules.add(new MessagesHandler(plugin));
        return modules;
    }

    /**
     * Adds a module to the internal map, keyed by the module's name in lowercase.
     *
     * @param module the module instance to add
     */
    private void addModule(Module module) {
        this.modules.put(module.name().toLowerCase(), module);
    }

    /**
     * Unloads the specified module and removes it from the internal modules map.
     *
     * @param module the module to unload and remove
     */
    private void removeModule(Module module) {
        module.unload();
        modules.remove(module.name());
    }

    private void loadAddedModules() {
        for(Module module : modules.values()) {
            try {
                module.load();
                Bukkit.getLogger().info("Loaded module "+module.name());
            } catch (InvalidLoadException e) {
                e.printStackTrace();
                Bukkit.getLogger().severe("Could not load module "+module.name());
            } catch (RefusedLoadException ignored) {}
        }
    }


}
