package it.heron.hpet.modules.pets.userpets.old;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.main.Utils;
import it.heron.hpet.modules.pets.pettypes.OldPetType;
import it.heron.hpet.userpets.childpet.ChildPet;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PassengerUserPet extends HeadUserPet {
    public PassengerUserPet(UUID owner, OldPetType type, ChildPet child) {
        super(owner, type, child);
    }

    @Override
    protected void tick() {
        setTaskID(Bukkit.getScheduler().scheduleSyncRepeatingTask(PetPlugin.getInstance(), () -> {
            try {
                if(getStep() % 2 == 0) {
                    if(getOwner() == null) remove();
                    int owner_id = Bukkit.getEntity(getOwner()).getEntityId();
                    PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().setPassengers(owner_id,getId()), getLocation().getWorld());
                }

                if(getStep() % 2 == 0) {

                    ItemStack itemStack = Utils.getCustomItem(getType().getSkins()[0]);
                    if(getColor() != null) {
                        itemStack = Utils.colorArmor(itemStack, getColor());
                    }

                    PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().equipItem(this.getId(), Utils.fromEquipSlot(getSlot()), itemStack), Bukkit.getEntity(getOwner()).getWorld());
                }

                if(getStep()%100 == 99) {
                    update();
                }

                PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().rotateHead(getId(), (int)Bukkit.getEntity(getOwner()).getLocation().getYaw(),0),getLocation().getWorld());
            } catch (Exception ignored) {}
            setStep(getStep()+1);
        }, 5,5));
    }
}
