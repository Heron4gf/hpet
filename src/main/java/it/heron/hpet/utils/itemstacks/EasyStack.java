package it.heron.hpet.utils.itemstacks;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.List;

public class EasyStack {

    protected ItemStack itemStack;

    /**
     * Constructs an ItemStack with the specified material, and optionally sets a custom display name and lore using Adventure Components.
     *
     * @param material the material for the ItemStack (must not be null)
     * @param name the display name to set, or null for default
     * @param lore the lore to set, or null for none
     */
    public EasyStack(@NonNull Material material, @Nullable Component name, @Nullable List<Component> lore) {
        // Create the ItemStack with the specified material.
        this.itemStack = new ItemStack(material);

        // Get and modify the ItemMeta.
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            // Set the display name and lore using Adventure's Component API.
            if(name != null) meta.displayName(name);
            if(lore != null) meta.lore(lore);
            itemStack.setItemMeta(meta);
        }
    }

    /**
     * Returns the underlying {@link ItemStack} instance created by this wrapper.
     *
     * @return the customized ItemStack
     */
    public ItemStack get() {
        return this.itemStack;
    }
}
