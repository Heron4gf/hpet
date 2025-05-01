package it.heron.hpet.modules.pets.pettypes;

import it.heron.hpet.utils.itemstacks.ModelDataStack;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class CustomModelPetType extends StackPetType {
    /**
     * Constructs a CustomModelPetType using the provided YAML configuration and key.
     *
     * @param configuration the YAML configuration for the pet type
     * @param key the identifier key for this pet type
     */
    public CustomModelPetType(YamlConfiguration configuration, String key) {
        super(configuration, key);
    }

    /**
     * Creates an ItemStack with a specified material and custom model data extracted from the given skin name.
     *
     * <p>The skin name must be formatted as "material customModelData", where "material" is a valid Material enum name and "customModelData" is an integer.</p>
     *
     * @param skinName the skin identifier in the format "material customModelData"
     * @return an ItemStack with the specified material and custom model data
     */
    @Override
    protected ItemStack makeSkin(String skinName) {
        // format: material customModelData
        String[] splitted = skinName.split(" ");
        Material material = Material.valueOf(splitted[0]);
        int customModelData = Integer.valueOf(splitted[1]);
        return new ModelDataStack(material, null, null, customModelData).get();
    }
}
