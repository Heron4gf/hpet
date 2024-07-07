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
import it.heron.hpet.userpets.PassengerUserPet;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import it.heron.hpet.userpets.UserPet;
import it.heron.hpet.Utils;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class PacketUtils {
    @Getter
    private Set<UserPet> pets = new HashSet<>();

    public List<UserPet> playerPets(UUID player) {
        List<UserPet> pts = new LinkedList<>();
        for(UserPet p : pets) {
            if(p != null && p.getOwner() != null) {
                if(p.getOwner().equals(player)) {
                    pts.add(p);
                }
            }
        }
        return pts;
    }

    public void spawnPet(Entity p, UserPet pet) {
        if(!pet.isInvisible()) {
            if(pet.getType().isMythicMob()) {
                try {
                    Entity e = MythicBukkit.inst().getAPIHelper().spawnMythicMob(pet.getType().getMythicMob(), Bukkit.getPlayer(pet.getOwner()).getLocation());
                    ((MythicUserPet)pet).setEntity(e);
                } catch(Exception ignored) {
                    Bukkit.getLogger().info("ERROR SPAWNING "+pet.getType().getName());
                }
            } else {
                if(pet.getType().isMob()) {
                    pet.setId(spawnPetEntity(pet.isGlow(), false, Utils.getCustomItem(pet.getType().getSkins()[0]), p.getLocation(), pet.getType().getEntityType(), pet.getSlot(), pet.getName(),null));
                } else {
                    if(pet instanceof PassengerUserPet) {
                        pet.setId(spawnPetEntity(pet.isGlow(), false, Utils.getCustomItem(pet.getType().getSkins()[0]), p.getLocation(), pet.getType().getEntityType(), pet.getSlot(), null,Bukkit.getEntity(pet.getOwner())));
                    } else {
                        Location spawn_location = p.getLocation();
                        if(pet.getLocation() != null) {
                            spawn_location = pet.getLocation();
                        }
                        pet.setId(spawnPetEntity(pet.isGlow(), false, Utils.getCustomItem(pet.getType().getSkins()[0]), spawn_location, pet.getType().getEntityType(), pet.getSlot(), null,null));
                    }
                }
                if(pet.getChild() != null) {
                    pet.getChild().setId(spawnPetEntity(pet.isGlow(), true, Utils.getCustomItem(pet.getType().getSkins()[0]), p.getLocation(), EntityType.ARMOR_STAND, pet.getSlot(), null,null));
                }
            }
        }

        if(!pet.isEnabled()) {
            if(!playerPets(p.getUniqueId()).isEmpty()) {
                List<UserPet> ps = playerPets(p.getUniqueId());
                for(UserPet userPet : ps) {
                    try {
                        if(pet.getType().getGroup().equals(userPet.getType().getGroup())) {
                            userPet.remove();
                        }
                    } catch (Exception ignored) {}
                }
            }
            pets.add(pet);
        }

    }

    public void removeFromPets(UserPet userPet) {
        pets.remove(userPet);
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
        return spawnPetEntity(glow, small, item, loc, entityType, slot, name, null);
    }

    public int spawnPetEntity(boolean glow, boolean small, ItemStack item, Location loc, EntityType entityType, EquipmentSlot slot, String name, Entity ride) {
        Entity e = loc.getWorld().spawnEntity(loc, entityType);
        int id = e.getEntityId();
        destroyQueue.add(id);

        e.setGravity(false);
        e.setGlowing(glow);
        if(e instanceof Ageable && small) {
            ((Ageable)e).setBaby();
        }

        if(item == null && name != null && !name.isEmpty()) {
            e.setCustomNameVisible(true);
            e.setCustomName(name);
        } else {
            e.setCustomNameVisible(false);
        }
        e.setInvulnerable(true);

        if(name != null && name.equals("hpet.leash") && entityType == EntityType.CHICKEN) {
            ((Chicken)e).setInvisible(true);
        }

        if(entityType == EntityType.ARMOR_STAND) {
            ArmorStand a = (ArmorStand) e;
            a.setSmall(small);
            a.setArms(true);
            a.setVisible(false);
            a.setMarker(true);
            if(ride != null) {
                ride.addPassenger(a);
            }
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

    public PacketContainer leashEntity(int attached, int holding) {
        PacketContainer entityAttach = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ATTACH_ENTITY);
        //entityAttach.getIntegers().writeDefaults();
        entityAttach.getIntegers().write(0, attached);
        entityAttach.getIntegers().write(1, holding);
        return entityAttach;
    }

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
        }
        return teleportPacket;

    }

    public PacketContainer rotateHead(int entityID, int yaw, int pitch) {

        PacketContainer rotateHead = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        rotateHead.getIntegers().write(0, entityID);
        rotateHead.getBytes().writeDefaults();
        rotateHead.getBytes().write(0, (byte) (yaw * 256.0F / 360.0F));
        return rotateHead;

    }

    public PacketContainer setInvisible(int entityID) {
        PacketContainer entityMetadata = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);

        entityMetadata.getIntegers().write(0, entityID);

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();

        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x20);
        entityMetadata.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
        return entityMetadata;
    }

    public PacketContainer setPassengers(int entityID, int... entities) {

        PacketContainer entityMount = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.MOUNT);
        entityMount.getIntegers().writeDefaults();

        entityMount.getIntegers().write(0, entityID);
        entityMount.getIntegers().writeSafely(1, entities.length);
        entityMount.getIntegerArrays().write(0, entities);

        return entityMount;

    }

    public abstract boolean isLegacy();

    public abstract WrappedDataWatcher getDataWatcher(PacketContainer entityMetadata);

    public abstract Vector3F getPose();



}
