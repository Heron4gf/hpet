package it.heron.hpet.utils.itemstacks;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ModelDataStack extends EasyStack {

    /**
     * Constructs an item stack with the specified material, display name, lore, and custom model data.
     *
     * @param material the material type for the item stack (must not be null)
     * @param name the display name for the item
     * @param lore the lore lines for the item
     * @param customModelData the custom model data value to assign to the item
     */
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
