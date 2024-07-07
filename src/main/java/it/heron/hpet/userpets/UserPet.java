/*
 * This file is part of HPET - Packet Based Pet Plugin
 *
 * TOS (Terms of Service)
 * You are not allowed to decompile, or redestribuite part of this code if not authorized by the original author.
 * You are not allowed to claim this resource as yours.
 */
package it.heron.hpet.userpets;

import com.comphenix.protocol.wrappers.EnumWrappers;

import it.heron.hpet.ChildPet;
import it.heron.hpet.Pet;
import it.heron.hpet.Utils;
import it.heron.hpet.abilities.AbilityExecutor;
import it.heron.hpet.animation.AnimationType;
import it.heron.hpet.operations.Coords;
import it.heron.hpet.pettypes.CosmeticType;
import it.heron.hpet.pettypes.PetType;
import lombok.Data;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import it.heron.hpet.animation.PetParticle;
import it.heron.hpet.api.events.PetRemoveEvent;
import it.heron.hpet.api.events.PetUpdateEvent;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public @Data
class UserPet {
    public boolean needRespawn() {return true;}

    private int id = 0;
    private UUID owner;

    private long step = 0;

    private PetType type;
    private boolean glow;
    private ChildPet child;
    private int taskID;
    private PetParticle particle;
    private Location location;
    private boolean invisible;
    private Color color;
    int leashId = -1;
    private Wolf follower = null;

    private String name = null;
    private int nameId = Utils.getRandomId();

    private Coords coords = Coords.calculate(0, 1, 1);

    public int getLevel() {
        return Pet.getApi().getPetLevel(Bukkit.getEntity(owner), type.getName());
    }

    public Location getTheoricalLocation() {
        return getCoords().getLoc(location);
    }

    public void spawn() {
        try {
            updateLevel();
        } catch(Exception ignored) {
            ignored.printStackTrace();
            Bukkit.getLogger().warning("There was an error spawning a Pet");
        }
    }

    public UserPet(UUID owner, PetType type, ChildPet child) {
        this.owner = owner;
        this.type = type;
        this.child = child;
        if(this.owner == null) return;
        this.location = Bukkit.getEntity(owner).getLocation();

        this.invisible = !this.type.isVisible();

        if(this.type.isCustomModelData()) {
            this.slot = EquipmentSlot.HEAD;
        }

        if(this.type instanceof CosmeticType) {
            this.color = ((CosmeticType)this.type).getColor();
        }

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
            if(Pet.getInstance().getPacketUtils().isLegacy()) {
                this.coords = Coords.calculate((int)newLoc.getYaw()-180, type.getDistance(), type.getNamey());
                newLoc.setYaw(newLoc.getYaw()-30);
            } else {
                this.coords = Coords.calculate((int)newLoc.getYaw(), type.getDistance(), type.getNamey());
            }
        }
            newLoc.setY(newLoc.getY()+this.type.getNamey()-1);

            if(slot == EquipmentSlot.HAND) Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(this.id, newLoc, true), Bukkit.getEntity(owner).getWorld());
            else {
                newLoc = newLoc.add(0, -type.getNamey(), 0);
                Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(this.id, newLoc, true), Bukkit.getEntity(owner).getWorld());
            }
            this.location = newLoc;

            // tp name id
            if(slot == EquipmentSlot.HAND) Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(this.nameId, getTheoricalLocation(), false), Bukkit.getEntity(owner).getWorld());
            else Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(getNameId(), newLoc.clone().add(0, type.getNamey()+1, 0), false), Bukkit.getEntity(owner).getWorld());
    }

    public void updateNameTag() {
        if(type.isBalloon()) {
            Location leashed_location;
            if(slot == EquipmentSlot.HAND) {
                leashed_location = getTheoricalLocation();
            } else {
                leashed_location = location.clone().add(0,-1,0);
            }
            leashId = Pet.getPackUtils().spawnPetEntity(false,false,null,leashed_location,EntityType.CHICKEN,null,"hpet.leash");
        }
        if(Pet.getInstance().getConfig().getBoolean("nametags.enable")) {
            String name = getName();
            try {
                if(name == null) {
                    if(Pet.getInstance().getConfig().getBoolean("nametags.defaultnametag")) {

                        String displayname = type.getDisplayName();
                        displayname = displayname.replace("%player%", Bukkit.getEntity(owner).getName()).replace("%level%",getLevel()+"");
                        name = Utils.color(Pet.getInstance().getNameFormat()).replace("%player%", Bukkit.getEntity(owner).getName()).replace("%name%", displayname).replace("%level%", getLevel() + "");
                        setName(name);
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

            ItemStack itemStack = Utils.getCustomItem(skins[skin]);
            if(color != null && !itemStack.getType().name().startsWith("LEATHER_")) {
                itemStack = Utils.colorArmor(itemStack, color);
            }

            Pet.getPackUtils().executePacket(Pet.getPackUtils().equipItem(this.id, Utils.fromEquipSlot(slot), itemStack), Bukkit.getEntity(owner).getWorld());
        }
    }

    public boolean isEnabled() {
        return this.step != 0;
    }

    public void spawnFollower() {
        Wolf wolf = getLocation().getWorld().spawn(getLocation(),Wolf.class);
        wolf.setInvisible(true);
        wolf.setInvulnerable(false);
        wolf.setOwner(Bukkit.getPlayer(getOwner()));
        wolf.setBaby();
        wolf.setSilent(true);
        this.follower = wolf;
    }

    public void update() {
        despawn();

        if(getType().getAnimation() == AnimationType.FOLLOW) {
            spawnFollower();
        }

        if(Pet.getInstance().getDisabledWorlds().contains(Bukkit.getEntity(owner).getWorld().getName())) {
            remove();
            return;
        }
        try {
            Bukkit.getPluginManager().callEvent(new PetUpdateEvent(Bukkit.getPlayer(owner), this));
        } catch (Exception ignored) {}
        this.abilities = this.type.getAbilities();
        for(AbilityExecutor a : abilities) a.execute(this);

        Pet.getPackUtils().spawnPet(Bukkit.getEntity(owner), this);
        if(this.invisible) {
            return;
        }
        updateNameTag();
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
        } catch(Exception ignored) {}
        update();
    }

    public void despawn() {
        try {
            despawn(Bukkit.getEntity(owner).getWorld());
        } catch (Exception exception) {
            despawn(Bukkit.getWorlds().get(0));
        }
    }
    public void despawn(World world) {
        if(follower != null) {
            follower.remove();
            follower = null;
        }

        for(AbilityExecutor a : abilities) {
            a.disable(this);
        }

        int id = this.id;
        this.id = 0;
        // destroy main pet
        if(slot == EquipmentSlot.HAND) Pet.getPackUtils().executePacket(Pet.getPackUtils().equipItem(id, EnumWrappers.ItemSlot.MAINHAND,null), world);
        else Pet.getPackUtils().executePacket(Pet.getPackUtils().equipItem(id, EnumWrappers.ItemSlot.HEAD,null),world);
        Pet.getPackUtils().executePacket(Pet.getPackUtils().destroyEntity(id), world);
        Utils.makeSureThisArmorstandIsNotRealPlease(id, world);
        // destroy main pet

        // destroy leash
        if(this.leashId != -1) {
            Utils.makeSureThisArmorstandIsNotRealPlease(this.leashId, world);
            int leashId = this.leashId;
            this.leashId = -1;
            Pet.getPackUtils().executePacket(Pet.getPackUtils().destroyEntity(leashId), world);
        }

        if(this.child != null) {
            int childid = this.child.getId();
            this.child.setId(0);

            // destroy child
            if(slot == EquipmentSlot.HAND) Pet.getPackUtils().executePacket(Pet.getPackUtils().equipItem(childid, EnumWrappers.ItemSlot.MAINHAND,null), world);
            else Pet.getPackUtils().executePacket(Pet.getPackUtils().equipItem(childid, EnumWrappers.ItemSlot.HEAD,null),world);
            Pet.getPackUtils().executePacket(Pet.getPackUtils().destroyEntity(childid), world);
            Utils.makeSureThisArmorstandIsNotRealPlease(childid, world);
            // destroy child
        }
        // destroy nametag
        for(World world1 : Bukkit.getWorlds()) {
            Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(nameId, new Location(world1,0,-10,0),false),world);
            break;
        }
        Pet.getPackUtils().executePacket(Pet.getPackUtils().destroyEntity(this.nameId), world);
        Utils.makeSureThisArmorstandIsNotRealPlease(nameId, world);
        // destroy nametag
    }

    public void remove() {
        try {
            Bukkit.getPluginManager().callEvent(new PetRemoveEvent(Bukkit.getPlayer(owner), this));
        } catch (Exception ignored) {}
        for(AbilityExecutor a : abilities) {
            a.disable(this);
        }
        Bukkit.getScheduler().cancelTask(this.taskID);

        despawn();
        if(Pet.getInstance().isUsingLegacySound()) {
            this.owner = null;
            return;
        }
        try {
            Bukkit.getEntity(owner).getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, Bukkit.getEntity(owner).getLocation(), 5);
        } catch (Exception ignored) {}
        this.owner = null;
        Pet.getPackUtils().removeFromPets(this);
    }

    public void destroyChild() {
        if(this.child == null) {
            return;
        }
        int id = this.child.getId();
        this.child = null;
        Pet.getPackUtils().executePacket(Pet.getPackUtils().destroyEntity(id), Bukkit.getEntity(owner).getWorld());
    }

    protected void tick() {
        this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Pet.getInstance(), new Runnable() {
            @Override
            public void run() {

                if(Bukkit.getEntity(owner) == null) {
                    remove();
                    return;
                }


                if(child != null) {
                    if(slot == EquipmentSlot.HAND) {
                        if(follower == null) {
                            child.teleport(Bukkit.getEntity(owner).getLocation().clone(), type.getAnimation().getAnimationValues());
                        } else {
                            child.teleport(follower.getLocation().clone(), type.getAnimation().getAnimationValues());
                        }
                    } else {
                        if(follower == null) {
                            child.teleport(coords.getLoc(Bukkit.getEntity(owner).getLocation().clone()), type.getAnimation().getAnimationValues());
                        } else {
                            child.teleport(coords.getLoc(follower.getLocation().clone()), type.getAnimation().getAnimationValues());
                        }
                    }
                }
                if(step%20 == 0) {
                    try {
                        if(type.isVisible()) setInvisible(Pet.getInstance().getVanish().isInvisible(Bukkit.getPlayer(owner)));
                    } catch (Exception ignored) {}
                }

                float[] steps;
                steps = type.getAnimation().getAnimationValues();
                if(steps[0] == -123f) {


                    Location theorical = getTheoricalLocation();
                    int y = Bukkit.getEntity(owner).getWorld().getHighestBlockYAt(theorical.getBlockX(), theorical.getBlockZ())+1;
                    if(Bukkit.getEntity(owner).getLocation().getBlockY()+5 < y) y = Bukkit.getEntity(owner).getLocation().getBlockY();
                    if(slot == EquipmentSlot.HAND) {
                        teleport(Bukkit.getEntity(owner).getLocation().add(0, y-Bukkit.getEntity(owner).getLocation().getBlockY(), 0));
                    } else {
                        teleport(coords.getLoc(Bukkit.getEntity(owner).getLocation().add(0, y-Bukkit.getEntity(owner).getLocation().getBlockY(), 0)));
                    }
                } else {
                    if(slot == EquipmentSlot.HAND) {
                        if(follower == null) {
                            teleport(Bukkit.getEntity(owner).getLocation().add(0, steps[(int) (step%steps.length)], 0));
                        } else {

                            teleport(follower.getLocation().add(0, steps[(int) (step%steps.length)], 0));
                        }
                    } else {
                        if(follower == null) {
                            teleport(coords.getLoc(Bukkit.getEntity(owner).getLocation().add(0, steps[(int) (step%steps.length)], 0)));
                        } else {
                            teleport(coords.getLoc(follower.getLocation().add(0, steps[(int) (step%steps.length)], 0)));
                        }

                    }
                }

                // set balloon leash
                if(type.isBalloon()) {
                    Location leashed_location;
                    if(slot == EquipmentSlot.HAND) {
                        leashed_location = getTheoricalLocation();
                    } else {
                        leashed_location = location.clone().add(0,-1,0);
                    }
                    leashed_location = leashed_location.add(0, type.getBallon_height(), 0);
                    Pet.getPackUtils().executePacket(Pet.getPackUtils().teleportEntity(leashId,leashed_location,false),Bukkit.getEntity(owner).getWorld());
                    Pet.getPackUtils().executePacket(Pet.getPackUtils().leashEntity(leashId, Bukkit.getEntity(owner).getEntityId()), location.getWorld());
                }

                if(!(Bukkit.getEntity(owner).getLocation().getX() == location.getX() && Bukkit.getEntity(owner).getLocation().getZ() == location.getZ())) {
                    if(particle != null && !invisible) particle.tick(getTheoricalLocation());
                }

                if(!invisible) {
                    if(step%100 == 99) {
                        update();
                    }
                }

                step++;
                animate();
            }
        }, 2, 2);
    }
}
