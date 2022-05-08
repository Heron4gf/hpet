/*
 * This file is part of HPET - Packet Based Pet Plugin
 *
 * TOS (Terms of Service)
 * You are not allowed to decompile, or redestribuite part of this code if not authorized by the original author.
 * You are not allowed to claim this resource as yours.
 */
package it.heron.hpet.packetutils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import io.lumine.xikage.mythicmobs.MythicMobs;
import it.heron.hpet.Pet;
import it.heron.hpet.userpets.MythicUserPet;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import it.heron.hpet.userpets.UserPet;
import it.heron.hpet.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class PacketUtils {
    @Getter
    private HashMap<UUID, UserPet> pets = new HashMap<>();
    public void removeFromPets(UUID owner) {
        this.pets.remove(owner);
    }

    public void spawnPet(Player p, UserPet pet) {
        if(Pet.getInstance().isDemo() && pets.size() > 9) {
            p.sendMessage("§eThis server is using a demo version of HPET, you cannot spawn more than 10 pets at the same time!");
            if(p.hasPermission("pet.admin")) {
                p.sendMessage("§eYou don't want limitations and customize your configs? Buy HPET! §bhttps://www.spigotmc.org/resources/%E2%AD%95%EF%B8%8F1-8-1-18-1%E2%AD%95%EF%B8%8Fhpet%E2%9C%8F%EF%B8%8Fcreate-unique-pets%E2%9D%97%EF%B8%8F20-off.93891/");
            }
            return;
        }

        if(pet.getType().isMythicMob()) {
            try {
                //System.out.println(MythicMobs.inst().getAPIHelper().getMythicMob(pet.getType().getMythicMob()));
                Entity e = MythicMobs.inst().getAPIHelper().spawnMythicMob(pet.getType().getMythicMob(), pet.getOwner().getLocation());
                ((MythicUserPet)pet).setEntity(e);
            } catch(Exception ignored) {
                System.out.println("ERROR SPAWNING "+pet.getType().getName());
            }
        } else {
            pet.setId(Utils.getRandomId());

            spawnPetEntity(pet.getId(), pet.isGlow(), false, Utils.getCustomItem(pet.getType().getSkins()[0]), p.getLocation(), pet.getType().getEntityType(), pet.getSlot());

            if(pet.getChild() != null) {
                spawnPetEntity(pet.getChild().getId(), pet.isGlow(), true, Utils.getCustomItem(pet.getType().getSkins()[0]), p.getLocation(), EntityType.ARMOR_STAND, pet.getSlot());
            }
        }
        pets.put(p.getUniqueId(), pet);

    }

    public PacketContainer setCustomName(int entityID, String name) {
        if(name == null) return null;

        PacketContainer entityMetadata = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);

        entityMetadata.getIntegers().write(0, entityID);

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher(entityMetadata.getWatchableCollectionModifier().read(0));

        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional
                .of(WrappedChatComponent
                        .fromChatMessage(name)[0].getHandle()));

        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);

        entityMetadata.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());

        return entityMetadata;
    }

    public void spawnPetEntity(int id, boolean glow, boolean small, ItemStack item, Location loc, EntityType entityType, EnumWrappers.ItemSlot slot) {
        PacketContainer[] packets;
        if(entityType == EntityType.ARMOR_STAND) {
            packets = new PacketContainer[]{spawnArmorstand(id, loc), equipItem(id, slot, item)};
         } else {
            packets = new PacketContainer[]{spawnEntity(id, loc, entityType), mobMetadata(id, glow)};
        }

        for(PacketContainer packet : packets) {
            executePacket(packet, loc.getWorld());
        }

        if(entityType != EntityType.ARMOR_STAND) return;
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(loc.getWorld().equals(p.getWorld())) {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(p, standardMetaData(id, p, small, glow));
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public abstract PacketContainer destroyEntity(int entityID);
    public abstract PacketContainer equipItem(int entityID, EnumWrappers.ItemSlot itemSlot, ItemStack item);

    public void executePacket(PacketContainer packet, World world) {
        if(packet == null) {
            return;
        }
        for(Player g : Bukkit.getOnlinePlayers()) {
            if(g.getWorld().equals(world)) {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(g, packet);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public PacketContainer teleportEntity(int entityID, Location location, boolean precise) {

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

    public PacketContainer entityLook(int entityID, double yaw) {
        PacketContainer entityLook = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        entityLook.getModifier().writeDefaults();
        entityLook.getIntegers().write(0, entityID);
        entityLook.getBytes().write(0, (byte)(yaw * 256.0F / 360.0F));

        return entityLook;
    }

    public PacketContainer moveEntity(int entityID, short x, short y, short z, float yaw) {
        if(y == 0) return null;
        PacketContainer entityMove = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
        entityMove.getModifier().writeDefaults();
        entityMove.getIntegers().write(0, entityID);
        entityMove.getShorts().write(0, x);
        entityMove.getShorts().write(1, y);
        entityMove.getShorts().write(2, z);

        entityMove.getBytes().write(0, (byte)(((yaw+180)*256)/360));


        return entityMove;
    }

    public PacketContainer spawnArmorstand(int entityID, Location location) {
        return spawnEntity(entityID, location, EntityType.ARMOR_STAND);
    }
    public PacketContainer spawnEntity(int entityID, Location location, EntityType type) {

        PacketContainer spawnPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);

        spawnPacket.getIntegers().write(0, entityID);
        spawnPacket.getIntegers().write(6, 0);

        spawnPacket.getEntityTypeModifier().write(0, type);

        spawnPacket.getDoubles().write(0, location.getX());
        spawnPacket.getDoubles().write(1, location.getY());
        spawnPacket.getDoubles().write(2, location.getZ());

        spawnPacket.getIntegers().write(4, 0);
        spawnPacket.getIntegers().write(5, (int) (location.getYaw() * 256.0F / 360.0F));

        spawnPacket.getUUIDs().write(0, UUID.randomUUID());

        return spawnPacket;

    }

    public abstract boolean isLegacy();


    public abstract int slotGlow();
    public abstract int slotSmall();
    public abstract WrappedDataWatcher getDataWatcher(PacketContainer entityMetadata);
    public abstract int slotHand();

    public abstract int slotSmallMob();

    public abstract Vector3F getPose();

    public PacketContainer standardMetaData(int entityID, Player p, boolean small, boolean glow) {
        PacketContainer entityMetadata = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        entityMetadata.getIntegers().write(0, entityID);
        return standardMetaData(entityMetadata, small, glow, Pet.getInstance().getVersionParser().getPlayerPackets(p));
    }
    public PacketContainer standardMetaData(PacketContainer entityMetadata, boolean small, boolean glow, PacketUtils protocol) {
        WrappedDataWatcher dataWatcher = getDataWatcher(entityMetadata);

            if (glow) {
                dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(slotGlow(), WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0xff);
            } else {
                dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(slotGlow(), WrappedDataWatcher.Registry.get(Byte.class)), (byte) 32);
            }

            if (small) {
                dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(slotSmall(), WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0xff);
            } else {
                dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(slotSmall(), WrappedDataWatcher.Registry.get(Byte.class)), (byte) 16);
            }

            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(slotHand(), WrappedDataWatcher.Registry.getVectorSerializer()), protocol.getPose());
            entityMetadata.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
        return entityMetadata;
    }

    public PacketContainer mobMetadata(int entityID, boolean glow) {
        PacketContainer entityMetadata = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
            entityMetadata.getIntegers().write(0, entityID);
            WrappedDataWatcher dataWatcher = getDataWatcher(entityMetadata);
            if (glow) {
                dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(slotGlow(), WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x40);
            }

            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(slotSmallMob(), WrappedDataWatcher.Registry.get(Boolean.class)), true);

            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(slotHand(), WrappedDataWatcher.Registry.getVectorSerializer()), getPose());
            entityMetadata.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
        return entityMetadata;
    }



}
