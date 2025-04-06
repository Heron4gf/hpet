package it.heron.hpet.modules.pets;

import it.heron.hpet.modules.abstracts.AbstractModule;
import it.heron.hpet.modules.abstracts.DefaultInstanceModule;
import it.heron.hpet.modules.pets.pettypes.PetType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

public class PetTypesHandler extends DefaultInstanceModule {

    private File PETS_FILE;
    private HashMap<String, PetType> loadedPetTypes = new HashMap<>();

    private void loadPetTypes(YamlConfiguration yamlConfiguration) {

    }

    public PetType petType(String name) {
        return this.loadedPetTypes.get(name);
    }

    public Collection<PetType> loadedPetTypes() {
        return this.loadedPetTypes.values();
    }

    @Override
    public String name() {
        return "PetsLoader";
    }

    @Override
    protected void onLoad() {
        PETS_FILE = new File(plugin.getDataFolder()+File.separator+"pets.yml");
        loadPetTypes(YamlConfiguration.loadConfiguration(PETS_FILE));
    }

    @Override
    protected void onUnload() {

    }
}
