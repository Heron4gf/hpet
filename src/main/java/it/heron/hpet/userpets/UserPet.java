/*
 * This file is part of HPET - Packet Based Pet Plugin
 *
 * TOS (Terms of Service)
 * You are not allowed to decompile, or redestribuite part of this code if not authorized by the original author.
 * You are not allowed to claim this resource as yours.
 */
package it.heron.hpet.userpets;

import com.comphenix.protocol.wrappers.EnumWrappers;

import it.heron.hpet.levels.LType;
import it.heron.hpet.levels.LevelEvents;
import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.main.Utils;
import it.heron.hpet.abilities.AbilityExecutor;
import it.heron.hpet.animation.AnimationType;
import it.heron.hpet.messages.Messages;
import it.heron.hpet.operations.Coords;
import it.heron.hpet.pettypes.CosmeticType;
import it.heron.hpet.pettypes.PetType;
import it.heron.hpet.userpets.childpet.ChildPet;
import lombok.Data;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import it.heron.hpet.animation.PetParticle;
import it.heron.hpet.api.events.PetRemoveEvent;
import it.heron.hpet.api.events.PetUpdateEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public @Data
class UserPet {
    public boolean needRespawn() {return true;}

    protected int id = 0;
    protected UUID owner;

    protected long step = 0;

    protected PetType type;
    protected boolean glow;
    protected ChildPet child;
    protected int taskID;
    protected PetParticle particle;
    protected Location location;
    protected boolean invisible;
    protected int level;
    protected Color color;
    protected int leashId = -1;
    protected Wolf follower = null;

    protected String name = null;
    protected int nameId = Utils.getRandomId();

    protected Coords coords = Coords.calculate(0, 1, 1);

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

    public Entity getOwnerEntity() {
        if(PetPlugin.getInstance().isUsingLegacyId()) {
            return Bukkit.getPlayer(owner);
        } else {
            return Bukkit.getEntity(owner);
        }
    }

    public UserPet(UUID owner, PetType type, ChildPet child) {
        this.owner = owner;
        this.type = type;
        this.child = child;
        if(this.owner == null) return;

        this.location = getOwnerEntity().getLocation();

        this.invisible = !this.type.isVisible();

        if(this.type.isCustomModelData()) {
            this.slot = EquipmentSlot.HEAD;
        }

        if(this.type instanceof CosmeticType) {
            this.color = ((CosmeticType)this.type).getColor();
        }

        PetPlugin.getInstance().getDatabase().getPetLevel(this.owner, this.type);
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

    public void incrementLevel() {
        if(type.getLtype() == LType.NONE) return;
        level++;
        PetPlugin.getInstance().getDatabase().setPetLevel(owner, type, level);
        if(level <= 1) return;
        Player player = Bukkit.getPlayer(owner);
        if(player == null) return;

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        for(String s : Messages.getList("levelup")) {
            player.sendMessage(s.replace("[level]", level+"").replace("[leveltype]", Messages.getMessage("leveltype."+type.getLtype().name())+" §7"+ LevelEvents.currentStat(this)+"/"+LevelEvents.getMaxStat(this)));
        }
    }

    public void teleport(Location newLoc) {

            newLoc.setYaw((newLoc.getYaw()+200+ PetPlugin.getInstance().getYawCalibration()+this.type.getYaw())%360);

        if(this.coords.getCos().getN() != (int)newLoc.getYaw()) {
            if(PetPlugin.getInstance().getPacketUtils().isLegacy()) {
                this.coords = Coords.calculate((int)newLoc.getYaw()-180, type.getDistance(), type.getNamey());
                newLoc.setYaw(newLoc.getYaw()-30);
            } else {
                this.coords = Coords.calculate((int)newLoc.getYaw(), type.getDistance(), type.getNamey());
            }
        }
            newLoc.setY(newLoc.getY()+this.type.getNamey()-1);

            if(slot == EquipmentSlot.HAND) PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().teleportEntity(this.id, newLoc, true), getOwnerEntity().getWorld());
            else {
                newLoc = newLoc.add(0, -type.getNamey(), 0);
                PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().teleportEntity(this.id, newLoc, true), getOwnerEntity().getWorld());
            }
            this.location = newLoc;

            // tp name id
            if(slot == EquipmentSlot.HAND) PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().teleportEntity(this.nameId, getTheoricalLocation(), false), getOwnerEntity().getWorld());
            else PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().teleportEntity(getNameId(), newLoc.clone().add(0, type.getNamey()+1, 0), false), getOwnerEntity().getWorld());
    }

    public void updateNameTag() {
        if(type.isBalloon()) {
            Location leashed_location;
            if(slot == EquipmentSlot.HAND) {
                leashed_location = getTheoricalLocation();
            } else {
                leashed_location = location.clone().add(0,-1,0);
            }
            leashId = PetPlugin.getPackUtils().spawnPetEntity(false,false,null,leashed_location,EntityType.CHICKEN,null,"hpet.leash");
        }
        if(PetPlugin.getInstance().getConfig().getBoolean("nametags.enable")) {
            String name = getName();
            try {
                if(name == null) {
                    if(PetPlugin.getInstance().getConfig().getBoolean("nametags.defaultnametag")) {

                        String displayname = type.getDisplayName();
                        displayname = displayname.replace("%player%", getOwnerEntity().getName()).replace("%level%",getLevel()+"");
                        name = Utils.color(PetPlugin.getInstance().getNameFormat()).replace("%player%", getOwnerEntity().getName()).replace("%name%", displayname).replace("%level%", getLevel() + "");
                        setName(name);
                        if(Bukkit.getPlayer(owner) != null) {
                            name = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(owner), name);
                        }
                        this.nameId = PetPlugin.getPackUtils().spawnPetEntity(false, false, null, getTheoricalLocation(), EntityType.ARMOR_STAND, null, name);
                    }
                } else {
                    if(Bukkit.getPlayer(owner) != null) {
                        name = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(owner), name);
                    }
                    this.nameId = PetPlugin.getPackUtils().spawnPetEntity(false, false, null, getTheoricalLocation(), EntityType.ARMOR_STAND, null, name);
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

            PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().equipItem(this.id, Utils.fromEquipSlot(slot), itemStack), getOwnerEntity().getWorld());
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

        if(PetPlugin.getInstance().getDisabledWorlds().contains(getOwnerEntity().getWorld().getName())) {
            remove();
            return;
        }
        try {
            Bukkit.getPluginManager().callEvent(new PetUpdateEvent(Bukkit.getPlayer(owner), this));
        } catch (Exception ignored) {}
        this.abilities = this.type.getAbilities();
        for(AbilityExecutor a : abilities) a.execute(this);

        PetPlugin.getPackUtils().spawnPet(getOwnerEntity(), this);
        if(this.invisible) {
            return;
        }
        updateNameTag();
    }

    private List<AbilityExecutor> abilities = new ArrayList<>();
    public void updateLevel() {
        if(!PetPlugin.getInstance().getConfig().getBoolean("useLevelEvents")) {
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
                if (PetPlugin.getInstance().getPetConfiguration().contains(type.getName() + ".particle")) {
                    recommended = Particle.valueOf(PetPlugin.getInstance().getPetConfiguration().getString(type.getName() + ".particle"));
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
            despawn(getOwnerEntity().getWorld());
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
        if(slot == EquipmentSlot.HAND) PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().equipItem(id, EnumWrappers.ItemSlot.MAINHAND,null), world);
        else PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().equipItem(id, EnumWrappers.ItemSlot.HEAD,null),world);
        PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().destroyEntity(id), world);
        Utils.makeSureThisArmorstandIsNotRealPlease(id, world);
        // destroy main pet

        // destroy leash
        if(this.leashId != -1) {
            Utils.makeSureThisArmorstandIsNotRealPlease(this.leashId, world);
            int leashId = this.leashId;
            this.leashId = -1;
            PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().destroyEntity(leashId), world);
        }

        if(this.child != null) {
            int childid = this.child.getId();
            this.child.setId(0);

            // destroy child
            if(slot == EquipmentSlot.HAND) PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().equipItem(childid, EnumWrappers.ItemSlot.MAINHAND,null), world);
            else PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().equipItem(childid, EnumWrappers.ItemSlot.HEAD,null),world);
            PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().destroyEntity(childid), world);
            Utils.makeSureThisArmorstandIsNotRealPlease(childid, world);
            // destroy child
        }
        // destroy nametag
        for(World world1 : Bukkit.getWorlds()) {
            PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().teleportEntity(nameId, new Location(world1,0,-10,0),false),world);
            break;
        }
        PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().destroyEntity(this.nameId), world);
        Utils.makeSureThisArmorstandIsNotRealPlease(nameId, world);
        // destroy nametag
    }

    public void remove() {
        if(PetPlugin.getInstance().getCachedConfigurationInfo().isPetLevellingEnabled()) {
            PetPlugin.getInstance().getDatabase().setPetLevel(owner, type, level);
        }
        try {
            Bukkit.getPluginManager().callEvent(new PetRemoveEvent(Bukkit.getPlayer(owner), this));
        } catch (Exception ignored) {}
        for(AbilityExecutor a : abilities) {
            a.disable(this);
        }
        Bukkit.getScheduler().cancelTask(this.taskID);

        despawn();
        if(PetPlugin.getInstance().isUsingLegacySound()) {
            this.owner = null;
            return;
        }
        try {
            getOwnerEntity().getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, getOwnerEntity().getLocation(), 5);
        } catch (Exception ignored) {}
        this.owner = null;
        PetPlugin.getPackUtils().removeFromPets(this);
    }

    public void destroyChild() {
        if(this.child == null) {
            return;
        }
        int id = this.child.getId();
        this.child = null;
        PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().destroyEntity(id), getOwnerEntity().getWorld());
    }

    protected void tick() {
        this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(PetPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {

                if(getOwnerEntity() == null) {
                    remove();
                    return;
                }


                if(child != null) {
                    if(slot == EquipmentSlot.HAND) {
                        if(follower == null) {
                            child.teleport(getOwnerEntity().getLocation().clone(), type.getAnimation().getAnimationValues());
                        } else {
                            child.teleport(follower.getLocation().clone(), type.getAnimation().getAnimationValues());
                        }
                    } else {
                        if(follower == null) {
                            child.teleport(coords.getLoc(getOwnerEntity().getLocation().clone()), type.getAnimation().getAnimationValues());
                        } else {
                            child.teleport(coords.getLoc(follower.getLocation().clone()), type.getAnimation().getAnimationValues());
                        }
                    }
                }
                if(step%20 == 0) {
                    try {
                        if(type.isVisible()) setInvisible(PetPlugin.getInstance().getVanish().isInvisible(Bukkit.getPlayer(owner)));
                    } catch (Exception ignored) {}
                }

                float[] steps;
                steps = type.getAnimation().getAnimationValues();
                if(steps[0] == -123f) {


                    Location theorical = getTheoricalLocation();
                    int y = getOwnerEntity().getWorld().getHighestBlockYAt(theorical.getBlockX(), theorical.getBlockZ())+1;
                    if(getOwnerEntity().getLocation().getBlockY()+5 < y) y = getOwnerEntity().getLocation().getBlockY();
                    if(slot == EquipmentSlot.HAND) {
                        teleport(getOwnerEntity().getLocation().add(0, y-getOwnerEntity().getLocation().getBlockY(), 0));
                    } else {
                        teleport(coords.getLoc(getOwnerEntity().getLocation().add(0, y-getOwnerEntity().getLocation().getBlockY(), 0)));
                    }
                } else {
                    if(slot == EquipmentSlot.HAND) {
                        if(follower == null) {
                            teleport(getOwnerEntity().getLocation().add(0, steps[(int) (step%steps.length)], 0));
                        } else {

                            teleport(follower.getLocation().add(0, steps[(int) (step%steps.length)], 0));
                        }
                    } else {
                        if(follower == null) {
                            teleport(coords.getLoc(getOwnerEntity().getLocation().add(0, steps[(int) (step%steps.length)], 0)));
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
                    PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().teleportEntity(leashId,leashed_location,false),getOwnerEntity().getWorld());
                    PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().leashEntity(leashId, getOwnerEntity().getEntityId()), location.getWorld());
                }

                if(!(getOwnerEntity().getLocation().getX() == location.getX() && getOwnerEntity().getLocation().getZ() == location.getZ())) {
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
