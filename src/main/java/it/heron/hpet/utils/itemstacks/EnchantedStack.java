package it.heron.hpet.utils.itemstacks;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class EnchantedStack extends EasyStack {

    public EnchantedStack(Material material, Component name, List<Component> lore, Enchantment enchant, int level) {
        super(material, name, lore);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            // Add the specified enchantment (ignoring level restrictions if necessary)
            meta.addEnchant(enchant, level, true);
            // Hide enchantment details (so the enchantments don't show in the item lore)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemStack.setItemMeta(meta);
        }
    }
}
