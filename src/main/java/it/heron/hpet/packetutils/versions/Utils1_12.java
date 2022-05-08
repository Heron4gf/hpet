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
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.UUID;

public class Utils1_12 extends Utils1_15 {

    @Override
    public boolean isLegacy() {
        return true;
    }

    public static void initDestroyListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Pet.getInstance(), new PacketType[]{PacketType.Play.Server.ENTITY_DESTROY}) {
            public void onPacketSending(PacketEvent event) {
                int id = ((int[])event.getPacket().getIntegerArrays().read(0))[0];
                Iterator var3 = Pet.getInstance().getPacketUtils().getPets().values().iterator();

                while(true) {
                    UserPet pet;
                    do {
                        if (!var3.hasNext()) {
                            return;
                        }

                        pet = (UserPet)var3.next();
                    } while(pet.getId() != id && (pet.getChild() == null || pet.getChild().getId() != id));

                    event.setCancelled(true);
                }
            }
        });
    }

    @Override
    public int slotSmallMob() {
        return 12;
    }

    @Override
    public int slotSmall() {return 11;}

    @Override
    public PacketContainer setCustomName(int entityID, String name) {
        if(name == null) return null;

        PacketContainer entityMetadata = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);

        entityMetadata.getIntegers().write(0, entityID);

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher(entityMetadata.getWatchableCollectionModifier().read(0));

        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.get(String.class)), name);

        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);

        entityMetadata.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());

        return entityMetadata;

    }

    @Override
    public PacketContainer teleportEntity(int entityID, Location location, boolean precise) {
        /*Location location = loc;
        location.setYaw(location.getYaw()+180);*/

        PacketContainer teleportPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT);

        teleportPacket.getIntegers().write(0, entityID);

        teleportPacket.getDoubles().write(0, location.getX());
        teleportPacket.getDoubles().write(1, location.getY());
        teleportPacket.getDoubles().write(2, location.getZ());
        if(precise) {
            teleportPacket.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
            //teleportPacket.getBytes().write(1, (byte) (location.getPitch() * 256.0F / 360.0F));
        }

        return teleportPacket;
    }

    @Override
    public PacketContainer spawnEntity(int entityID, Location location, EntityType type) {
        Entity as = location.getWorld().spawn(location, type.getEntityClass());
        as.setGravity(false);
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
    public PacketContainer spawnArmorstand(int entityID, Location location) {
        PacketContainer spawnPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        spawnPacket.getIntegers().write(0, entityID);

        spawnPacket.getIntegers().write(6, 78);
        spawnPacket.getIntegers().write(7, 0);
        spawnPacket.getDoubles().write(0, location.getX());
        spawnPacket.getDoubles().write(1, location.getY());
        spawnPacket.getDoubles().write(2, location.getZ());
        spawnPacket.getIntegers().write(4, 0);
        spawnPacket.getIntegers().write(5, (int)(location.getYaw() * 256.0F / 360.0F));
        spawnPacket.getUUIDs().write(0, UUID.randomUUID());
        return spawnPacket;
    }

    @Override
    public Vector3F getPose() {return new Vector3F(-44.0F, 34.0F, 1.0F);}

    @Override
    public int slotHand() {return 15;}


}
