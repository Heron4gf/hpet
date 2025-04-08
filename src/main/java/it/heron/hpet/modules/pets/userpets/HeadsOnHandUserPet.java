package it.heron.hpet.modules.pets.userpets;

import it.heron.hpet.modules.pets.pettypes.HeadPetType;
import it.heron.hpet.modules.pets.userpets.abstracts.FakeEntitiesUserPet;
import org.bukkit.entity.Entity;

public class HeadsOnHandUserPet extends FakeEntitiesUserPet {

    public HeadsOnHandUserPet(HeadPetType petType, Entity owner, int level) {
        super(petType, owner, level);
    }

}
