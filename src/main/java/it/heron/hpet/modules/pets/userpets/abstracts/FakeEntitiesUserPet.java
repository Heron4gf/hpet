package it.heron.hpet.modules.pets.userpets.abstracts;

import it.heron.hpet.modules.pets.pettypes.PetType;
import it.heron.hpet.modules.pets.userpets.fakeentities.FakeEntity;
import it.heron.hpet.modules.pets.userpets.nametags.INametag;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public abstract class FakeEntitiesUserPet extends AbstractUserPet {

    private FakeEntity fakeEntity;
    private INametag nametag;

    public FakeEntitiesUserPet(PetType petType, Entity owner, int level) {
        super(petType, owner, level);
    }

    @Override
    protected Location getNextLocation() {
        return null;
    }


    @Override
    public void onSpawn() {
        this.fakeEntity.spawn(this.location);
        this.id = fakeEntity.getId();
    }

    @Override
    public void onDespawn() {
        this.fakeEntity.despawn();
        this.id = -1;
    }

    public void tick() {
        super.tick();
    }
}
