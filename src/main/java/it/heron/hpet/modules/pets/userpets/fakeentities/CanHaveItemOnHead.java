package it.heron.hpet.modules.pets.userpets.fakeentities;

import org.bukkit.inventory.ItemStack;

public interface CanHaveItemOnHead {
    /**
 * Sets the specified item as the head item for the implementing entity.
 *
 * @param itemStack the item to be placed on the head
 */
void setHeadItem(ItemStack itemStack);
}
