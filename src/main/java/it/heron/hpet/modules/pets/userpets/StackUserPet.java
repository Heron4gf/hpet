package it.heron.hpet.modules.pets.userpets;

import it.heron.hpet.modules.pets.pettypes.StackPetType;
import it.heron.hpet.modules.pets.userpets.fakeentities.CanHaveItemOnHead;
import org.bukkit.entity.Entity;

public class StackUserPet extends HandUserPet {


    /**
     * Creates a new StackUserPet with the specified pet type, owner, and level.
     *
     * @param petType the type of stackable pet
     * @param owner the entity that owns this pet
     * @param level the level of the pet
     */
    public StackUserPet(StackPetType petType, Entity owner, int level) {
        super(petType, owner, level);
    }

    /**
     * Updates the pet's head item to the next item in the stack.
     *
     * This method casts the underlying entity to {@code CanHaveItemOnHead} and sets its head item
     * to the result of {@code nextStack()}, effectively cycling the displayed item on the pet's head.
     */
    @Override
    protected void switchStack() {
        CanHaveItemOnHead entity = (CanHaveItemOnHead) this.fakeEntity;
        entity.setHeadItem(nextStack());
    }

}
