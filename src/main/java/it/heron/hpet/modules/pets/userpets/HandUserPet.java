package it.heron.hpet.modules.pets.userpets;

import it.heron.hpet.modules.pets.pettypes.StackPetType;
import it.heron.hpet.modules.pets.userpets.abstracts.FakeEntitiesUserPet;
import it.heron.hpet.modules.pets.userpets.fakeentities.CanHaveItemInHand;
import it.heron.hpet.modules.pets.userpets.fakeentities.CanHaveItemOnHead;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class HandUserPet extends FakeEntitiesUserPet {

    private int skinSteps = 0;

    public HandUserPet(StackPetType petType, Entity owner, int level) {
        super(petType, owner, level);
    }

    protected void switchStack() {
        CanHaveItemInHand entity = (CanHaveItemInHand) this.fakeEntity;
        entity.setHeldItem(nextStack());
    }

    @Override
    public void tick() {
        super.tick();
        switchStack();
    }

    protected ItemStack nextStack() {
        ItemStack[] skins = ((StackPetType)petType).getSkins();
        skinSteps++;
        return skins[skinSteps%skins.length];
    }

}
