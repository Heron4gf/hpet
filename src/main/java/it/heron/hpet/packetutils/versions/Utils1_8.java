package it.heron.hpet.packetutils.versions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.packetutils.PacketUtils;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

public class Utils1_8 extends Utils1_12 {


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
    public PacketContainer standardMetaData(PacketContainer entityMetadata, PacketUtils protocol) {
        return null;
    }

    @Override
    public int spawnPetEntity(boolean glow, boolean small, ItemStack item, Location loc, EntityType entityType, EquipmentSlot slot, String name) {
        Entity e = loc.getWorld().spawnEntity(loc, entityType);
        int id = e.getEntityId();
        destroyQueue.add(id);
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
            a.setMarker(true);
            a.setRightArmPose(new EulerAngle(0,1,0));
            a.setItemInHand(item);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                e.remove();
            }
        }.runTaskLater(PetPlugin.getInstance(), 5);
        return id;
    }

    @Override
    public int spawnPetEntity(boolean glow, boolean small, ItemStack item, Location loc, EntityType entityType, EquipmentSlot slot, String name, Entity ride) {
        Entity e = loc.getWorld().spawnEntity(loc, entityType);
        int id = e.getEntityId();
        destroyQueue.add(id);

        //e.setGlowing(glow);
        if(e instanceof Ageable && small) {
            ((Ageable)e).setBaby();
        }

        if(item == null && name != null && !name.isEmpty()) {
            e.setCustomNameVisible(true);
            e.setCustomName(name);
        } else {
            e.setCustomNameVisible(false);
        }

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
            if (slot != null) {
                switch (slot) {
                    case HAND:
                        a.setItemInHand(item);
                        break;
                    case HEAD:
                        a.setHelmet(item);
                        break;
                    case CHEST:
                        a.setChestplate(item);
                        break;
                    case LEGS:
                        a.setLeggings(item);
                        break;
                    case FEET:
                        a.setBoots(item);
                        break;
                }
            }
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
                }.runTaskLater(PetPlugin.getInstance(), 6);
            }
        }.runTaskLater(PetPlugin.getInstance(), 5);
        return id;
    }

    private int getFixedPoint(double d) {
        d = d*32d;
        return (int)d;
    }


}
