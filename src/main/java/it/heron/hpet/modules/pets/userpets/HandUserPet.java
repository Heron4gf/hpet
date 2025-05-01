package it.heron.hpet.modules.pets.userpets;

import it.heron.hpet.modules.pets.pettypes.StackPetType;
import it.heron.hpet.modules.pets.userpets.abstracts.FakeEntitiesUserPet;
import it.heron.hpet.modules.pets.userpets.fakeentities.CanHaveItemInHand;
import it.heron.hpet.modules.pets.userpets.fakeentities.CanHaveItemOnHead;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class HandUserPet extends FakeEntitiesUserPet {

    private int skinSteps = 0;

    /**
     * Constructs a HandUserPet with the specified pet type, owner, and level.
     *
     * @param petType the type of pet, providing item skins to cycle through
     * @param owner the entity that owns this pet
     * @param level the level of the pet
     */
    public HandUserPet(StackPetType petType, Entity owner, int level) {
        super(petType, owner, level);
    }

    /**
     * Updates the held item of the internal entity to the next item skin in the cycle.
     */
    protected void switchStack() {
        CanHaveItemInHand entity = (CanHaveItemInHand) this.fakeEntity;
        entity.setHeldItem(nextStack());
    }

    /**
     * Advances the pet's held item to the next skin on each tick.
     *
     * Calls the superclass tick logic, then updates the held item to visually cycle through available item skins.
     */
    @Override
    public void tick() {
        super.tick();
        switchStack();
    }

    /**
     * Returns the next item skin in the cycle for the pet to hold.
     *
     * Increments the internal skin index and retrieves the corresponding {@link ItemStack}
     * from the pet type's skins array, cycling back to the start when the end is reached.
     *
     * @return the next {@link ItemStack} to be displayed as the pet's held item
     */
    protected ItemStack nextStack() {
        ItemStack[] skins = ((StackPetType)petType).getSkins();
        skinSteps++;
        return skins[skinSteps%skins.length];
    }

}
