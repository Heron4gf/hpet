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
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.UUID;

public class Utils1_8 extends Utils1_12 {


    @Override
    public Vector3F getPose() { return new Vector3F(0, 0, 0);}


    @Override
    public PacketContainer teleportEntity(int entityID, Location loc, boolean precise) {
        Location location = loc;
        location.setYaw(location.getYaw()+180);

        PacketContainer teleportPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT);

        teleportPacket.getIntegers().write(0, entityID);

        teleportPacket.getIntegers().write(1, getFixedPoint(location.getX()));
        teleportPacket.getIntegers().write(2, getFixedPoint(location.getY()));
        teleportPacket.getIntegers().write(3, getFixedPoint(location.getZ()));
        if(precise) {
            teleportPacket.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
        }
        return teleportPacket;
    }

    @Override
    public int spawnPetEntity(boolean glow, boolean small, ItemStack item, Location loc, EntityType entityType, EquipmentSlot slot, String name) {
        Entity e = loc.getWorld().spawnEntity(loc, entityType);
        if(e instanceof Ageable && small) {
            ((Ageable)e).setBaby();
        }
        e.setCustomNameVisible(name != null);
        e.setCustomName(name);

        if(entityType == EntityType.ARMOR_STAND) {
            ArmorStand a = (ArmorStand) e;
            a.setSmall(small);
            a.setArms(true);
            a.setVisible(false);
            executePacket(standardMetaData(e.getEntityId(), null), e.getWorld());
            a.setItemInHand(item);
        }

        int id = e.getEntityId();
        destroyQueue.add(id);
        new BukkitRunnable() {
            @Override
            public void run() {
                e.remove();
            }
        }.runTaskLater(Pet.getInstance(), 5);
        return id;
    }

    @Override
    public PacketContainer standardMetaData(PacketContainer entityMetadata, PacketUtils protocol) {
        return null;
        /*WrappedDataWatcher dataWatcher = getDataWatcher(entityMetadata);

        dataWatcher.setObject(14, protocol.getPose().getX());
        dataWatcher.setObject(14, protocol.getPose().getY());
        dataWatcher.setObject(14, protocol.getPose().getZ());
        entityMetadata.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
        return entityMetadata;*/
    }

    private int getFixedPoint(double d) {
        d = d*32d;
        return (int)d;
    }


}
