package it.heron.hpet.userpets;

import com.comphenix.protocol.events.PacketContainer;
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
    }

    @Override
    public void updateNameTag() {
        if(Pet.getInstance().getConfig().getBoolean("nametags.enable")) {
            PacketContainer[] packets = {Pet.getPackUtils().spawnArmorstand(getNameId(), getLocation()), Pet.getPackUtils().standardMetaData(getNameId(), getOwner(), true, false), Pet.getPackUtils().setCustomName(getNameId(), getName())};
            for(PacketContainer packet : packets) {
                Pet.getPackUtils().executePacket(packet, getOwner().getWorld());
            }
        }
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
        //if (newLoc.getX() == getLocation().getX() && newLoc.getZ() == getLocation().getZ() && newLoc.getY() == getLocation().getY()) return;
        /*if (getStep() % 10 == 0) {
            Pet.getPackUtils().executePacket(Pet.getPackUtils().entityLook(getId(), newLoc.getYaw()), getOwner().getWorld());
        }
        setLocation(newLoc);
        newLoc.setYaw(newLoc.getYaw() + 50);
        newLoc.setY(newLoc.getY()+getType().getNamey()-1);
        if (getCoords().getCos().getN() != (int) newLoc.getYaw()) {
            setCoords(Coords.calculate((int) newLoc.getYaw(), getType().getDistance(), getType().getNamey()));
        }
        Location loc = getCoords().getLoc(newLoc);
        Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(getNameId(), loc, false), getOwner().getWorld());

        Location sure = loc.add(0, -getType().getNamey(), 0);
        Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(getId(), sure, false), getOwner().getWorld());

        for(int j : children) {
            Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(j, sure, false), getOwner().getWorld());
        }*/
        if(entity == null) return;
        Location loc = getCoords().getLoc(newLoc);
        Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(getNameId(), loc, false), getOwner().getWorld());
        entity.teleport(loc.add(0, -getType().getNamey(), 0));
    }

    @Override
    public void despawn(World world) {
        if(entity == null) return;
        entity.remove();
        entity = null;
        Pet.getPackUtils().executePacket(Pet.getPackUtils().destroyEntity(getNameId()), world);
    }

    /*@Override
    public void move(float step, float laststep) {
        super.move(step, laststep);
        short s =(short)(((step*32) - (laststep*32))*128);
        for(int i : children) {
            Pet.getPackUtils().executePacket(Pet.getPackUtils().moveEntity(i, s), getOwner().getWorld());
        }
    }*/

    /*public static void initDestroyListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Pet.getInstance(), new PacketType[]{PacketType.Play.Server.ENTITY_DESTROY}) {
            public void onPacketSending(PacketEvent event) {
                int id = event.getPacket().getIntLists().read(0).get(0);
                if(!beingRemoved.isEmpty() && beingRemoved.contains(id)) {
                    event.setCancelled(true);
                    beingRemoved.remove(id);
                }
            }
        });
    }*/

}
