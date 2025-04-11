package it.heron.hpet.utils.heads;

import org.bukkit.inventory.ItemStack;

public abstract class AbstractHead implements Head {

    private ItemStack itemStack = null;

    @Override
    public ItemStack get() {
        if(itemStack == null) itemStack = generate();
        return itemStack;
    }

    public abstract ItemStack generate();

}
