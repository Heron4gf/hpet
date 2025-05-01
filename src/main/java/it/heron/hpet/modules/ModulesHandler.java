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

    public boolean hasModule(String moduleName) {
        return this.modules.containsKey(moduleName);
    }

    public Module moduleByName(String moduleName) {
        return this.modules.get(moduleName.toLowerCase());
    }

    public void loadModules() {
        for(Module module : validModules()) {
            addModule(module);
        }
        loadAddedModules();
    }

    public void unloadModules() {
        new ArrayList<>(modules.values()).forEach(this::removeModule);
    }

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

    private void addModule(Module module) {
        this.modules.put(module.name().toLowerCase(), module);
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
