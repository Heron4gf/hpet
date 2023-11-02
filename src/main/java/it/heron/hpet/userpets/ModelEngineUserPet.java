package it.heron.hpet.userpets;

import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import it.heron.hpet.ChildPet;
import it.heron.hpet.Pet;
import it.heron.hpet.pettypes.PetType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ModelEngineUserPet extends MythicUserPet {

    @Getter @Setter
    private ModeledEntity modeledEntity;

    @Getter @Setter
    private ActiveModel activeModel;

    public ModelEngineUserPet(Player owner, PetType type, ChildPet child) {
        super(owner, type, child);
    }

    @Override
    public void teleport(Location newLoc) {
        if(modeledEntity == null) return;

        try {
            AnimationHandler animationHandler = activeModel.getAnimationHandler();
            if(animationHandler.hasFinishedAllAnimations()) {
                if (newLoc.getZ() == getLocation().getZ() && newLoc.getX() == getLocation().getX()) {
                    animationHandler.playAnimation("idle", 1, 1, 1, true);
                } else {
                    animationHandler.playAnimation("walk",1,1,1,true);
                }
            }
        } catch (Exception ignored) {}

        Location loc = getCoords().getLoc(newLoc);
        Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(getNameId(), loc, true), Bukkit.getPlayer(getOwner()).getWorld());
        getEntity().teleport(loc.add(0, -getType().getNamey(), 0));
    }
}
