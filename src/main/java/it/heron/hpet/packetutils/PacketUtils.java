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
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.*;
import io.lumine.mythic.bukkit.MythicBukkit;
import it.heron.hpet.Pet;
import it.heron.hpet.userpets.MythicUserPet;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import it.heron.hpet.userpets.UserPet;
import it.heron.hpet.Utils;
import org.bukkit.scheduler.BukkitRunnable;

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
                Entity e = MythicBukkit.inst().getAPIHelper().spawnMythicMob(pet.getType().getMythicMob(), pet.getOwner().getLocation());
                ((MythicUserPet)pet).setEntity(e);
            } catch(Exception ignored) {
                Bukkit.getLogger().info("ERROR SPAWNING "+pet.getType().getName());
            }
        } else {
            if(pet.getType().isMob()) {
                pet.setId(spawnPetEntity(pet.isGlow(), false, Utils.getCustomItem(pet.getType().getSkins()[0]), p.getLocation(), pet.getType().getEntityType(), pet.getSlot(), pet.getName()));
            } else {
                pet.setId(spawnPetEntity(pet.isGlow(), false, Utils.getCustomItem(pet.getType().getSkins()[0]), p.getLocation(), pet.getType().getEntityType(), pet.getSlot(), null));
            }
            if(pet.getChild() != null) {
                pet.getChild().setId(spawnPetEntity(pet.isGlow(), true, Utils.getCustomItem(pet.getType().getSkins()[0]), p.getLocation(), EntityType.ARMOR_STAND, pet.getSlot(), null));
            }
        }
        pets.put(p.getUniqueId(), pet);

    }


    @Getter
    protected Set<Integer> destroyQueue = new HashSet<>();
    public void initDestroyListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Pet.getInstance(), new PacketType[]{PacketType.Play.Server.ENTITY_DESTROY}) {
            public void onPacketSending(PacketEvent event) {
                int id = ((int[])event.getPacket().getIntegerArrays().read(0))[0];
                if(destroyQueue.contains(id)) {
                    event.setCancelled(true);
                }
            }
        });
    }

    public int spawnPetEntity(boolean glow, boolean small, ItemStack item, Location loc, EntityType entityType, EquipmentSlot slot, String name) {
        Entity e = loc.getWorld().spawnEntity(loc, entityType);
        int id = e.getEntityId();
        destroyQueue.add(id);

        e.setGravity(false);
        e.setGlowing(glow);
        if(e instanceof Ageable && small) {
            ((Ageable)e).setBaby();
        }
        e.setCustomNameVisible(name != null);
        e.setCustomName(name);
        e.setInvulnerable(true);

        if(entityType == EntityType.ARMOR_STAND) {
            ArmorStand a = (ArmorStand) e;
            a.setSmall(small);
            a.setArms(true);
            a.setVisible(false);
            a.setMarker(true);
            executePacket(standardMetaData(e.getEntityId(), null), e.getWorld());
            if(slot != null) a.getEquipment().setItem(slot, item);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                e.remove();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        destroyQueue.remove(id);
                    }
                }.runTaskLater(Pet.getInstance(), 6);
            }
        }.runTaskLater(Pet.getInstance(), 5);
        return id;
    }


    public abstract PacketContainer destroyEntity(int entityID);
    public abstract PacketContainer equipItem(int entityID, EnumWrappers.ItemSlot slot, ItemStack item);

    public void executePacket(PacketContainer packet, World world) {
        if(packet == null) {
            return;
        }
        for(Player g : Bukkit.getOnlinePlayers()) {
            if(g.getWorld().equals(world)) {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(g, packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public abstract int slotHand();

    public PacketContainer standardMetaData(int entityID, Player p) {
        PacketContainer entityMetadata = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        entityMetadata.getIntegers().write(0, entityID);
        return standardMetaData(entityMetadata, Pet.getInstance().getVersionParser().getPlayerPackets(p));
    }
    public PacketContainer standardMetaData(PacketContainer entityMetadata, PacketUtils protocol) {
        WrappedDataWatcher dataWatcher = getDataWatcher(entityMetadata);


        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(slotHand(), WrappedDataWatcher.Registry.getVectorSerializer()), protocol.getPose());
        entityMetadata.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
        return entityMetadata;
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

    public abstract boolean isLegacy();

    public abstract WrappedDataWatcher getDataWatcher(PacketContainer entityMetadata);

    public abstract Vector3F getPose();



}
