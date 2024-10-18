package it.heron.hpet.userpets;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.operations.Coords;
import it.heron.hpet.pettypes.PetType;
import it.heron.hpet.userpets.childpet.ChildPet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MobUserPet extends UserPet {

    public MobUserPet(Player owner, PetType type, ChildPet child) {
        super(owner.getUniqueId(), type, child);
    }

    @Override
    public void animate() {}

    @Override
    public Location getTheoricalLocation() {
        return getLocation();
    }

    @Override
    public void teleport(Location newLoc) {
        newLoc.setYaw((newLoc.getYaw()+160+ PetPlugin.getInstance().getYawCalibration()+getType().getYaw())%360);

        if(getCoords().getCos().getN() != (int)newLoc.getYaw()) {
            setCoords(Coords.calculate((int)newLoc.getYaw(), getType().getDistance(), getType().getNamey()));
        }
        newLoc.setY(newLoc.getY()+getType().getNamey()-1);
        newLoc = getCoords().getLoc(newLoc);

        newLoc.setYaw((newLoc.getYaw()-60)%360);
        PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().teleportEntity(getId(), newLoc.add(0, -getType().getNamey(), 0), true), Bukkit.getPlayer(getOwner()).getWorld());
        setLocation(newLoc);
    }

    @Override
    public void updateNameTag() {}

}
