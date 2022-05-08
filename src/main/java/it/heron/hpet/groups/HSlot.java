package it.heron.hpet.groups;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public @Data
abstract class HSlot {

    private String name;
    private String displayName = null;

    public abstract ItemStack getIcon(Player p);

}
