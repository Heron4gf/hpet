package it.heron.hpet.packetutils.versions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import it.heron.hpet.ChildPet;
import it.heron.hpet.Pet;
import it.heron.hpet.userpets.UserPet;
import it.heron.hpet.packetutils.PacketUtils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.UUID;

public class Utils1_8 extends Utils1_12 {


    @Override
    public Vector3F getPose() { return new Vector3F(0, 0, 0);}

    @Override
    public PacketContainer spawnArmorstand(int entityID, Location location) {
        ArmorStand as = (ArmorStand)location.getWorld().spawn(location, ArmorStand.class);
        as.setGravity(false);
        as.setVisible(false);
        as.setMarker(true);
        as.setArms(true);
        Iterator var4 = Pet.getInstance().getPacketUtils().getPets().values().iterator();

        while(var4.hasNext()) {
            UserPet pet = (UserPet)var4.next();
            if (pet.getId() == entityID) {
                pet.setId(as.getEntityId());
            }

            if (pet.getChild() != null && pet.getChild().getId() == entityID) {
                ChildPet child = pet.getChild();
                child.setId(as.getEntityId());
                pet.setChild(child);
                as.setSmall(true);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                as.remove();
            }
        }.runTaskLater(Pet.getInstance(), 20);
        return null;
    }


    @Override
    public PacketContainer teleportEntity(int entityID, Location loc, boolean precise) {
        Location location = loc;
        location.setYaw(location.getYaw()+180);

        PacketContainer teleportPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT);

        teleportPacket.getIntegers().write(0, entityID);

        //teleportPacket.getIntegers().write(1, (int)EntityType.ARMOR_STAND.getTypeId());
        teleportPacket.getIntegers().write(1, getFixedPoint(location.getX()));
        teleportPacket.getIntegers().write(2, getFixedPoint(location.getY()));
        teleportPacket.getIntegers().write(3, getFixedPoint(location.getZ()));
        if(precise) {
            teleportPacket.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
            //teleportPacket.getBytes().write(1, (byte) (location.getPitch() * 256.0F / 360.0F));
        }
        return teleportPacket;
    }

    @Override
    public PacketContainer standardMetaData(PacketContainer entityMetadata, boolean small, boolean glow, PacketUtils protocol) {
        WrappedDataWatcher dataWatcher = getDataWatcher(entityMetadata);
        if (glow) {
            dataWatcher.setObject(0, (byte)0xff);
        } else {
            dataWatcher.setObject(0, (byte)32);
        }

        if (small) {
            dataWatcher.setObject(10, (byte)0xff);
        } else {
            dataWatcher.setObject(10, (byte)0x10);
        }

        dataWatcher.setObject(14, protocol.getPose().getX());
        dataWatcher.setObject(14, protocol.getPose().getY());
        dataWatcher.setObject(14, protocol.getPose().getZ());
        entityMetadata.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
        return entityMetadata;
    }

    private int getFixedPoint(double d) {
        d = d*32d;
        return (int)d;
    }


}
