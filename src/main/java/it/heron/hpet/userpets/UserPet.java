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
import it.heron.hpet.operations.Coords;
import it.heron.hpet.pettypes.PetType;
import lombok.Data;
import org.bukkit.*;
import org.bukkit.entity.Player;
import it.heron.hpet.animation.PetParticle;
import it.heron.hpet.api.events.PetRemoveEvent;
import it.heron.hpet.api.events.PetUpdateEvent;

import java.util.ArrayList;
import java.util.List;

public @Data
class UserPet {
    public boolean needRespawn() {return false;}

    private int id = 0;
    private Player owner;

    private long step;
    //private long skin;

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
        updateLevel();
    }

    public UserPet(Player owner, PetType type, ChildPet child) {
        //if(type.isFollow()) tick = followTick();
        this.owner = owner;
        this.step = 0l;
        //this.skin = 0l;
        this.type = type;
        if(Pet.getInstance().getConfig().getBoolean("nametags.defaultnametag")) {
            this.name = type.getDisplayName();
        }
        this.child = child;
        if(this.owner == null) return;
        this.location = this.owner.getLocation();

        if(this.type.isCustomModelData()) this.slot = EnumWrappers.ItemSlot.HEAD;


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

            newLoc.setYaw(newLoc.getYaw()+200+Pet.getInstance().getYawCalibration()+this.type.getYaw());

        if(this.coords.getCos().getN() != (int)newLoc.getYaw()) {
            this.coords = Coords.calculate((int)newLoc.getYaw(), type.getDistance(), type.getNamey());
        }
            newLoc.setY(newLoc.getY()+this.type.getNamey()-1);


            if(slot == EnumWrappers.ItemSlot.MAINHAND) Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(this.id, newLoc, true), owner.getWorld());
            else Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(this.id, newLoc.add(0, -type.getNamey(), 0), true), owner.getWorld());
            this.location = newLoc;

            if(slot == EnumWrappers.ItemSlot.MAINHAND) Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(this.nameId, this.coords.getLoc(newLoc), false), owner.getWorld());
            else Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(getNameId(), newLoc.add(0, type.getNamey()+1, 0), false), getOwner().getWorld());
    }

    public void updateNameTag() {
        if(Pet.getInstance().getConfig().getBoolean("nametags.enable")) {

            String name = null;
            try {
                name = Utils.color(Pet.getInstance().getNameFormat()).replace("%player%", owner.getName()).replace("%name%", this.name).replace("%level%", getLevel()+"");
            } catch(Exception ignored) {}

            PacketContainer[] packets = {Pet.getPackUtils().spawnArmorstand(this.nameId, this.location), Pet.getPackUtils().standardMetaData(this.nameId, owner, true, false), Pet.getPackUtils().setCustomName(this.nameId, name)};
            for(PacketContainer packet : packets) {
                Pet.getPackUtils().executePacket(packet, owner.getWorld());
            }
        }
    }

    private EnumWrappers.ItemSlot slot = EnumWrappers.ItemSlot.MAINHAND;

    public void animate() {
        String[] skins = type.getSkins();

        int skin = (int) ((this.step/7)%(skins.length));
        if(slot == EnumWrappers.ItemSlot.HEAD) skin = 0;

        if(this.step % 7 == 0) {
            Pet.getPackUtils().executePacket(Pet.getPackUtils().equipItem(this.id, slot, Utils.getCustomItem(skins[skin])), owner.getWorld());
        }
    }
    public void update() {
        Bukkit.getPluginManager().callEvent(new PetUpdateEvent(owner, this));
        despawn();
        if(this.invisible) {
            return;
        }
        Pet.getPackUtils().spawnPet(owner, this);
        updateNameTag();
        //teleport(owner.getLocation());

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
        if(this.child != null) {
            int childid = this.child.getId();
            this.child.setId(0);
            Pet.getPackUtils().executePacket(Pet.getPackUtils().destroyEntity(childid), world);
        }
        Pet.getPackUtils().executePacket(Pet.getPackUtils().destroyEntity(this.nameId), world);
    }

    public void remove() {
        Bukkit.getPluginManager().callEvent(new PetRemoveEvent(owner, this));
        Bukkit.getScheduler().cancelTask(this.taskID);
        Pet.getPackUtils().removeFromPets(owner.getUniqueId());
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


    public void move(Location location) {

        short x = (short)(((location.getX()*32)-(this.location.getX()*32))*128);
        short y = (short)(((location.getY()*32)-(this.location.getY()*32))*128);
        short z = (short)(((location.getZ()*32)-(this.location.getZ()*32))*128);

        Pet.getPackUtils().executePacket(Pet.getPackUtils().moveEntity(this.id, x,y,z,location.getYaw()), owner.getWorld());
        Pet.getPackUtils().executePacket(Pet.getPackUtils().moveEntity(this.nameId, x,y,z,location.getYaw()), owner.getWorld());
        this.location = location;
    }

    protected void tick() {
        int repeatDelay = Pet.instance.getConfig().getInt("teleport-delay", 2);
        if (repeatDelay < 2){
            // 2 is the minimum allowed value
            repeatDelay = 2;
        }

        this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Pet.getInstance(), new Runnable() {
            @Override
            public void run() {

                if(child != null) {
                    if(slot == EnumWrappers.ItemSlot.MAINHAND) {
                        child.teleport(owner.getLocation().clone(), type.getAnimation().getAnimationValues());
                    } else {
                        child.teleport(coords.getLoc(owner.getLocation().clone()), type.getAnimation().getAnimationValues());
                    }
                }
                if(step%20 == 0) {
                    setInvisible(Pet.getInstance().getVanish().isInvisible(owner) || Pet.getInstance().getVanish().isVanished(owner));
                }
                float[] steps = type.getAnimation().getAnimationValues();

                if(!(owner.getLocation().getX() == location.getX() && owner.getLocation().getZ() == location.getZ())) {
                    if(particle != null && !invisible) particle.tick(getTheoricalLocation());
                }

                if(slot == EnumWrappers.ItemSlot.MAINHAND) {
                    teleport(owner.getLocation().add(0, steps[(int) (step%steps.length)], 0));
                } else {
                    teleport(coords.getLoc(owner.getLocation().add(0, steps[(int) (step%steps.length)], 0)));
                }

                step++;
                animate();
            }
        }, 2, repeatDelay);
    }




}
