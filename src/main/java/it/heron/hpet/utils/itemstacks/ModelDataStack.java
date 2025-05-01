package it.heron.hpet.utils.itemstacks;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ModelDataStack extends EasyStack {

    public ModelDataStack(@NonNull Material material, Component name, List<Component> lore, int customModelData) {
        super(material, name, lore);
        // Retrieve the ItemMeta and set the custom model data on it.
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(customModelData);
            itemStack.setItemMeta(meta);
        }
    }
}
