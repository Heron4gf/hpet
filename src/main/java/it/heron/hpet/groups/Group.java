/*
 * This file is part of HPET - Packet Based Pet Plugin
 *
 * TOS (Terms of Service)
 * You are not allowed to decompile, or redestribuite part of this code if not authorized by the original author.
 * You are not allowed to claim this resource as yours.
 */
package it.heron.hpet.groups;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import it.heron.hpet.pettypes.PetType;
import it.heron.hpet.main.Utils;
import it.heron.hpet.messages.Messages;

import java.util.ArrayList;
import java.util.List;

public @Data
class Group extends HSlot {
    private PetType[] type;
    private ItemStack icon;

    public Group(String name, PetType[] type) {
        setName(name);
        setDisplayName("§a§l"+name);
        this.type = type;
    }

    @Override
    public ItemStack getIcon(Player p) {
        ItemStack stack = Utils.getCustomItem(this.type[0].getSkins()[0]).clone();

        stack.setAmount(type.length);

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(getDisplayName());

        List<String> lore =  new ArrayList<>();
        lore.add("");
        for(PetType t : this.type) {
            lore.add(" §a● "+t.getDisplayName());
        }
        lore.add("");
        lore.add(Messages.getMessage("group.open"));

        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }
}
