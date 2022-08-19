package it.heron.hpet.userpets;

import it.heron.hpet.*;
import it.heron.hpet.operations.Coords;
import it.heron.hpet.pettypes.PetType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MobUserPet extends UserPet {

    public MobUserPet(Player owner, PetType type, ChildPet child) {
        super(owner, type, child);
    }

    @Override
    public void animate() {}

    @Override
    public Location getTheoricalLocation() {
        return getLocation();
    }

    @Override
    public void teleport(Location newLoc) {
        newLoc.setYaw(newLoc.getYaw()+200+Pet.getInstance().getYawCalibration()+getType().getYaw());

        if(getCoords().getCos().getN() != (int)newLoc.getYaw()) {
            setCoords(Coords.calculate((int)newLoc.getYaw(), getType().getDistance(), getType().getNamey()));
        }
        newLoc.setY(newLoc.getY()+getType().getNamey()-1);
        newLoc = getCoords().getLoc(newLoc);


        Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(getId(), newLoc.add(0, -getType().getNamey(), 0), true), getOwner().getWorld());
        setLocation(newLoc);
    }

    @Override
    public void updateNameTag() {}

}
