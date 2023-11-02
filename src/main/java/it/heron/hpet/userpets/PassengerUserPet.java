package it.heron.hpet.userpets;

import it.heron.hpet.ChildPet;
import it.heron.hpet.Pet;
import it.heron.hpet.pettypes.PetType;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.UUID;

public class PassengerUserPet extends UserPet {
    public PassengerUserPet(UUID owner, PetType type, ChildPet child) {
        super(owner, type, child);
    }

    @Override
    protected void tick() {
        setTaskID(Bukkit.getScheduler().scheduleSyncRepeatingTask(Pet.getInstance(), () -> {
            try {
                if(getStep() % 2 == 0) {
                    if(getOwner() == null) remove();
                    int owner_id = Bukkit.getEntity(getOwner()).getEntityId();
                    Pet.getPackUtils().executePacket(Pet.getPackUtils().setPassengers(owner_id,getId()), getLocation().getWorld());
                }
                Pet.getPackUtils().executePacket(Pet.getPackUtils().rotateHead(getId(), (int)Bukkit.getEntity(getOwner()).getLocation().getYaw(),0),getLocation().getWorld());
            } catch (Exception ignored) {}
            setStep(getStep()+1);
        }, 5,5));
    }
}
