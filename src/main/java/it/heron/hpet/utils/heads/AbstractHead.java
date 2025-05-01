package it.heron.hpet.utils.heads;

import org.bukkit.inventory.ItemStack;

public abstract class AbstractHead implements Head {

    private ItemStack itemStack = null;

    /**
     * Returns the cached {@link ItemStack} instance, generating it if necessary.
     *
     * If the item stack has not been created yet, this method calls {@code generate()} to produce and cache it before returning.
     *
     * @return the generated or cached {@link ItemStack}
     */
    @Override
    public ItemStack get() {
        if(itemStack == null) itemStack = generate();
        return itemStack;
    }

    /**
 * Generates and returns a new {@link ItemStack} instance representing the head.
 *
 * @return a newly created {@link ItemStack} for this head
 */
public abstract ItemStack generate();

}
