package it.heron.hpet.modules.pets.pettypes;

import it.heron.hpet.utils.itemstacks.ModelDataStack;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class CustomModelPetType extends StackPetType {
    public CustomModelPetType(YamlConfiguration configuration, String key) {
        super(configuration, key);
    }

    @Override
    protected ItemStack makeSkin(String skinName) {
        // format: material customModelData
        String[] splitted = skinName.split(" ");
        Material material = Material.valueOf(splitted[0]);
        int customModelData = Integer.valueOf(splitted[1]);
        return new ModelDataStack(material, null, null, customModelData).get();
    }
}
