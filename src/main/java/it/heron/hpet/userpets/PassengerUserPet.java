package it.heron.hpet.userpets;

import it.heron.hpet.ChildPet;
import it.heron.hpet.Pet;
import it.heron.hpet.Utils;
import it.heron.hpet.pettypes.PetType;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

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

                if(getStep() % 2 == 0) {

                    ItemStack itemStack = Utils.getCustomItem(getType().getSkins()[0]);
                    if(getColor() != null) {
                        itemStack = Utils.colorArmor(itemStack, getColor());
                    }

                    Pet.getPackUtils().executePacket(Pet.getPackUtils().equipItem(this.getId(), Utils.fromEquipSlot(getSlot()), itemStack), Bukkit.getEntity(getOwner()).getWorld());
                }

                if(getStep()%100 == 99) {
                    update();
                }

                Pet.getPackUtils().executePacket(Pet.getPackUtils().rotateHead(getId(), (int)Bukkit.getEntity(getOwner()).getLocation().getYaw(),0),getLocation().getWorld());
            } catch (Exception ignored) {}
            setStep(getStep()+1);
        }, 5,5));
    }
}
