package it.heron.hpet.userpets;

import it.heron.hpet.ChildPet;
import it.heron.hpet.Pet;
import it.heron.hpet.pettypes.PetType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MythicUserPet extends MobUserPet {

    @Override
    public boolean needRespawn() {return true;}

    @Setter @Getter
    private int[] children = {};

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

    public MythicUserPet(Player owner, PetType type, ChildPet child) {
        super(owner, type, child);
    }

    //public static Set<Integer> beingRemoved = new HashSet<>();

    @Override
    public void teleport(Location newLoc) {
        if(entity == null) return;
        Location loc = getCoords().getLoc(newLoc);
        Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(getNameId(), loc, true), getOwner().getWorld());
        entity.teleport(loc.add(0, -getType().getNamey(), 0));
    }

    @Override
    public void despawn(World world) {
        if(entity == null) return;
        entity.remove();
        entity = null;
        Pet.getPackUtils().executePacket(Pet.getPackUtils().destroyEntity(getNameId()), world);
    }

}
