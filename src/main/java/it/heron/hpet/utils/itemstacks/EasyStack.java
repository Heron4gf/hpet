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

    public ItemStack get() {
        return this.itemStack;
    }
}
