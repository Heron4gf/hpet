package it.heron.hpet.modules.pets.userpets.old;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.pets.pettypes.OldPetType;
import it.heron.hpet.userpets.childpet.ChildPet;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MythicUserPet extends MobUserPet {

    @Override
    public boolean needRespawn() {return true;}

    @Setter @Getter
    private int[] children = {};

    @Getter
    private Entity entity;
    public void setEntity(Entity e) {
        entity = e;
        entity.setGravity(false);
        entity.setInvulnerable(false);
    }


    @Override
    public Location getLocation() {
        return entity.getLocation();
    }

    public MythicUserPet(Player owner, OldPetType type, ChildPet child) {
        super(owner, type, child);
    }

    //public static Set<Integer> beingRemoved = new HashSet<>();

    @Override
    public void teleport(Location newLoc) {
        if(entity == null) return;
        Location loc = getCoords().getLoc(newLoc);
        PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().teleportEntity(getNameId(), loc, true), Bukkit.getPlayer(getOwner()).getWorld());
        entity.teleport(loc.add(0, -getType().getNamey(), 0));
    }

    @Override
    public void despawn(World world) {
        if(getFollower() != null) {
            getFollower().remove();
            setFollower(null);
        }
        if(entity == null) return;
        entity.remove();
        entity = null;
        PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().destroyEntity(getNameId()), world);
    }

}
