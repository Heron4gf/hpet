package it.heron.hpet.modules.pets.userpets;

import it.heron.hpet.modules.pets.pettypes.HeadPetType;
import it.heron.hpet.modules.pets.pettypes.PetType;
import it.heron.hpet.modules.pets.userpets.abstracts.FakeEntitiesUserPet;
import org.bukkit.entity.Entity;

public class HeadsOnHandUserPet extends FakeEntitiesUserPet {

    private HeadPetType petType;

    public HeadsOnHandUserPet(PetType petType, Entity owner, int level) {
        super(petType, owner, level);
        if(!(petType instanceof HeadPetType)) throw new IllegalArgumentException("HeadsOnHandUserPet can only be instantiated with a HeadPetType");
    }

    @Override
    public PetType getPetType() {
        return petType;
    }
}
