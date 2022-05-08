package it.heron.hpet.userpets;

import it.heron.hpet.*;
import it.heron.hpet.operations.Coords;
import it.heron.hpet.pettypes.PetType;
import org.bukkit.Bukkit;
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
            //setLocation(newLoc);
            newLoc.setYaw(newLoc.getYaw() + 50);
            newLoc.setY(newLoc.getY()+getType().getNamey()-1);
            if (getCoords().getCos().getN() != (int) newLoc.getYaw()) {
                setCoords(Coords.calculate((int) newLoc.getYaw(), getType().getDistance(), getType().getNamey()));
            }
            Location loc = getCoords().getLoc(newLoc);

            if(getStep()%100 != 0 && !Pet.getInstance().getPacketUtils().isLegacy()) {
                move(loc.add(0,-getType().getNamey(),0));
                return;
            }

            setLocation(loc);
            Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(getNameId(), loc, false), getOwner().getWorld());
            Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(getId(), loc.add(0, -getType().getNamey(), 0), false), getOwner().getWorld());
    }

    @Override
    public void updateNameTag() {
        if(Pet.getInstance().getConfig().getBoolean("nametags.enable")) {
            //PacketContainer[] packets = {Pet.getPackUtils().spawnArmorstand(this.nameId, this.location), Pet.getPackUtils().standardMetaData(this.nameId, owner, true, false), Pet.getPackUtils().setCustomName(this.nameId, this.name)};
            Pet.getPackUtils().executePacket(Pet.getPackUtils().setCustomName(getId(), getName()), getOwner().getWorld());
        }
    }

}
