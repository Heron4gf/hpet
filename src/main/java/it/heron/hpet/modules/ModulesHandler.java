package it.heron.hpet.modules;

import it.heron.hpet.modules.abstracts.Module;
import it.heron.hpet.modules.exceptions.InvalidLoadException;
import it.heron.hpet.modules.exceptions.RefusedLoadException;
import it.heron.hpet.modules.hooks.HeadDatabaseModule;
import it.heron.hpet.modules.hooks.ItemsAdderModule;
import it.heron.hpet.modules.hooks.PapiModule;
import it.heron.hpet.modules.hooks.VaultHook;
import it.heron.hpet.modules.invisibilityintegration.InvisibilityHandler;
import it.heron.hpet.modules.pets.PetTypesHandler;
import it.heron.hpet.modules.pets.PetsHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ModulesHandler {

    private JavaPlugin plugin;
    private final HashMap<String, Module> modules = new HashMap<>();

    public ModulesHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean hasModule(String moduleName) {
        return this.modules.containsKey(moduleName);
    }

    public Module moduleByName(String moduleName) {
        return this.modules.get(moduleName);
    }

    public void loadModules() {
        for(Module module : validModules()) {
            addModule(module);
        }
        loadAddedModules();
    }

    public void unloadModules() {
        for(Module module : modules.values()) {
            removeModule(module);
        }
    }

    private Collection<Module> validModules() {
        List<Module> modules = new ArrayList<>();
        modules.add(new InvisibilityHandler());
        modules.add(new PapiModule());
        modules.add(new VaultHook());
        modules.add(new ItemsAdderModule());
        modules.add(new HeadDatabaseModule());
        modules.add(new DatabaseModule());
        modules.add(new PetTypesHandler());
        modules.add(new PetsHandler());
        return modules;
    }

    private void addModule(Module module) {
        this.modules.put(module.name(), module);
    }

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
