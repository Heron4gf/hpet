/*
 * This file is part of HPET - Packet Based Pet Plugin
 *
 * TOS (Terms of Service)
 * You are not allowed to decompile, or redestribuite part of this code if not authorized by the original author.
 * You are not allowed to claim this resource as yours.
 */
package it.heron.hpet.userpets;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;

import it.heron.hpet.ChildPet;
import it.heron.hpet.Pet;
import it.heron.hpet.Utils;
import it.heron.hpet.abilities.AbilityExecutor;
import it.heron.hpet.animation.AnimationType;
import it.heron.hpet.operations.Coords;
import it.heron.hpet.pettypes.PetType;
import lombok.Data;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import it.heron.hpet.animation.PetParticle;
import it.heron.hpet.api.events.PetRemoveEvent;
import it.heron.hpet.api.events.PetUpdateEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;

public @Data
class UserPet {
    public boolean needRespawn() {return true;}

    private int id = 0;
    private Player owner;

    private long step;

    private PetType type;
    private boolean glow;
    private ChildPet child;
    private int taskID;
    private PetParticle particle;
    private Location location;
    private boolean invisible;

    private String name = null;
    private int nameId = Utils.getRandomId();

    private Coords coords = Coords.calculate(0, 1, 1);

    public int getLevel() {
        return Pet.getApi().getPetLevel(owner, type.getName());
    }

    public Location getTheoricalLocation() {
        return getCoords().getLoc(location);
    }

    public void spawn() {
        try {
            updateLevel();
        } catch(Exception ignored) {
            Bukkit.getLogger().warning("There was an error spawning a Pet");
        }
    }

    public UserPet(Player owner, PetType type, ChildPet child) {
        this.owner = owner;
        this.step = 0l;
        this.type = type;
        this.child = child;
        if(this.owner == null) return;
        this.location = this.owner.getLocation();

        if(this.type.isCustomModelData()) this.slot = EquipmentSlot.HEAD;
        spawn();
        tick();
    }

    public void setInvisible(boolean state) {
        if(state == this.invisible) {
            return;
        }
        this.invisible = state;
        destroyChild();
        update();
    }

    public void teleport(Location newLoc) {

            newLoc.setYaw((newLoc.getYaw()+200+Pet.getInstance().getYawCalibration()+this.type.getYaw())%360);

        if(this.coords.getCos().getN() != (int)newLoc.getYaw()) {
            this.coords = Coords.calculate((int)newLoc.getYaw(), type.getDistance(), type.getNamey());
        }
            newLoc.setY(newLoc.getY()+this.type.getNamey()-1);

            if(slot == EquipmentSlot.HAND) Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(this.id, newLoc, true), owner.getWorld());
            else Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(this.id, newLoc.add(0, -type.getNamey(), 0), true), owner.getWorld());
            this.location = newLoc;

            if(slot == EquipmentSlot.HAND) Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(this.nameId, getTheoricalLocation(), false), owner.getWorld());
            else Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(getNameId(), newLoc.add(0, type.getNamey()+1, 0), false), getOwner().getWorld());
    }

    public void updateNameTag() {
        if(Pet.getInstance().getConfig().getBoolean("nametags.enable")) {

            String name = getName();
            try {
                if(name == null) {
                    if(Pet.getInstance().getConfig().getBoolean("nametags.defaultnametag")) {

                        String displayname = type.getDisplayName();
                        displayname = displayname.replace("%player%", owner.getName()).replace("%level%",getLevel()+"");
                        name = Utils.color(Pet.getInstance().getNameFormat()).replace("%player%", owner.getName()).replace("%name%", displayname).replace("%level%", getLevel() + "");
                        this.nameId = Pet.getPackUtils().spawnPetEntity(false, false, null, getTheoricalLocation(), EntityType.ARMOR_STAND, null, name);
                    }
                } else {
                    this.nameId = Pet.getPackUtils().spawnPetEntity(false, false, null, getTheoricalLocation(), EntityType.ARMOR_STAND, null, name);
                }
            } catch(Exception ignored) {}

        }
    }

    private EquipmentSlot slot = EquipmentSlot.HAND;

    public void animate() {
        String[] skins = type.getSkins();

        int skin = (int) ((this.step/7)%(skins.length));

        if(this.step % 7 == 0) {
            Pet.getPackUtils().executePacket(Pet.getPackUtils().equipItem(this.id, Utils.fromEquipSlot(slot), Utils.getCustomItem(skins[skin])), owner.getWorld());
        }
    }
    public void update() {
        despawn();

        if(Pet.getInstance().getDisabledWorlds().contains(owner.getWorld().getName())) {
            remove();
            return;
        }

        Bukkit.getPluginManager().callEvent(new PetUpdateEvent(owner, this));
        if(this.invisible) {
            return;
        }
        Pet.getPackUtils().spawnPet(owner, this);
        updateNameTag();

        this.abilities = this.type.getAbilities();
        for(AbilityExecutor a : abilities) a.execute(this);
    }

    private List<AbilityExecutor> abilities = new ArrayList<>();
    public void updateLevel() {
        if(!Pet.getInstance().getConfig().getBoolean("useLevelEvents")) {
            update();
            return;
        }
        try {
            int level = getLevel();
            if (level > 2) {
                this.setChild(new ChildPet());
            }
            if (level > 4) {
                Particle recommended = Particle.SNOWBALL;
                if (Pet.getInstance().getPetConfiguration().contains(type.getName() + ".particle")) {
                    recommended = Particle.valueOf(Pet.getInstance().getPetConfiguration().getString(type.getName() + ".particle"));
                }
                this.setParticle(new PetParticle(recommended));
            }
            if (level > 6) {
                this.setGlow(true);
            }
            update();
        } catch(Exception ignored) {}
    }

    public void despawn() {
        despawn(owner.getWorld());
    }
    public void despawn(World world) {

        for(AbilityExecutor a : abilities) {
            a.disable(this);
        }

        int id = this.id;
        this.id = 0;
        Pet.getPackUtils().executePacket(Pet.getPackUtils().destroyEntity(id), world);
        Utils.makeSureThisArmorstandIsNotRealPlease(id, world);
        if(this.child != null) {
            int childid = this.child.getId();
            this.child.setId(0);
            Pet.getPackUtils().executePacket(Pet.getPackUtils().destroyEntity(childid), world);
            Utils.makeSureThisArmorstandIsNotRealPlease(childid, world);
        }
        Pet.getPackUtils().executePacket(Pet.getPackUtils().destroyEntity(this.nameId), world);
        Utils.makeSureThisArmorstandIsNotRealPlease(nameId, world);
        Pet.getPackUtils().removeFromPets(owner.getUniqueId());
    }

    public void remove() {
        Bukkit.getPluginManager().callEvent(new PetRemoveEvent(owner, this));
        Bukkit.getScheduler().cancelTask(this.taskID);

        despawn();
        if(Pet.getInstance().isUsingLegacySound()) {
            return;
        }
        this.owner.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, this.owner.getLocation(), 5);
    }



    public void destroyChild() {
        if(this.child == null) {
            return;
        }
        int id = this.child.getId();
        this.child = null;
        Pet.getPackUtils().executePacket(Pet.getPackUtils().destroyEntity(id), owner.getWorld());
    }
    protected void tick() {
        this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Pet.getInstance(), new Runnable() {
            @Override
            public void run() {

                if(child != null) {
                    if(slot == EquipmentSlot.HAND) {
                        child.teleport(owner.getLocation().clone(), type.getAnimation().getAnimationValues());
                    } else {
                        child.teleport(coords.getLoc(owner.getLocation().clone()), type.getAnimation().getAnimationValues());
                    }
                }
                if(step%20 == 0) {
                    setInvisible(Pet.getInstance().getVanish().isInvisible(owner));
                }
                float[] steps;
                steps = type.getAnimation().getAnimationValues();
                if(steps[0] == -123f) {


                    Location theorical = getTheoricalLocation();
                    int y = owner.getWorld().getHighestBlockYAt(theorical.getBlockX(), theorical.getBlockZ())+1;
                    if(owner.getLocation().getBlockY()+5 < y) y = owner.getLocation().getBlockY();
                    if(slot == EquipmentSlot.HAND) {
                        teleport(owner.getLocation().add(0, y-owner.getLocation().getBlockY(), 0));
                    } else {
                        teleport(coords.getLoc(owner.getLocation().add(0, y-owner.getLocation().getBlockY(), 0)));
                    }
                } else {
                    if(slot == EquipmentSlot.HAND) {
                        teleport(owner.getLocation().add(0, steps[(int) (step%steps.length)], 0));
                    } else {
                        teleport(coords.getLoc(owner.getLocation().add(0, steps[(int) (step%steps.length)], 0)));
                    }
                }

                if(!(owner.getLocation().getX() == location.getX() && owner.getLocation().getZ() == location.getZ())) {
                    if(particle != null && !invisible) particle.tick(getTheoricalLocation());
                }

                step++;
                animate();
            }
        }, 2, 2);
    }




}
