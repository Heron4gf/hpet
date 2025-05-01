package it.heron.hpet.modules.pets.userpets.abstracts;

import it.heron.hpet.modules.pets.pettypes.PetType;
import it.heron.hpet.modules.pets.userpets.fakeentities.FakeEntity;
import it.heron.hpet.modules.pets.userpets.nametags.INametag;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public abstract class FakeEntitiesUserPet extends AbstractUserPet {

    protected FakeEntity fakeEntity;
    protected INametag nametag;

    /**
     * Constructs a FakeEntitiesUserPet with the specified pet type, owner, and level.
     *
     * @param petType the type of the pet
     * @param owner the entity that owns the pet
     * @param level the level of the pet
     */
    public FakeEntitiesUserPet(PetType petType, Entity owner, int level) {
        super(petType, owner, level);
    }


    /**
     * Spawns the pet's fake entity at its current location and updates the pet's ID.
     */
    @Override
    public void onSpawn() {
        this.fakeEntity.spawn(this.location);
        this.id = fakeEntity.getId();
    }

    /**
     * Despawns the pet's fake entity and resets its ID.
     */
    @Override
    public void onDespawn() {
        this.fakeEntity.despawn();
        this.id = -1;
    }

}
