package it.heron.hpet.modules.pets.userpets.fakeentities;

import org.bukkit.inventory.ItemStack;

public interface CanHaveItemInHand {
    /**
 * Sets or updates the item currently held in hand.
 *
 * @param itemStack the item to be held
 */
void setHeldItem(ItemStack itemStack);
}
