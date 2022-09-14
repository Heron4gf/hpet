package it.heron.hpet.abilities;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import it.heron.hpet.Pet;
import it.heron.hpet.Utils;
import it.heron.hpet.userpets.UserPet;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class AbilityExecutor implements Listener {

    private String[] args;
    private AbilityType a;

    private int chance;
    private int repeat;

    public AbilityExecutor(String value) {
        if(value.contains(":")) {
            args = value.split(":");
        } else {
            args = new String[]{value};
        }
        a = AbilityType.valueOf(args[0]);

        int chance = 100;
        for(String s : args) {
            try {
                if(s.contains("%")) chance = Integer.parseInt(s.replace("%", ""));
            } catch(Exception ignored) {}
        }
        int repeat = 10;
        for(String s : args) {
            try {
                int m = -1;
                if(s.contains("m")) {
                    m = s.indexOf("m");
                    repeat = 60*Integer.parseInt(s.substring(0, m));
                }
                if(s.contains("s")) {
                    if(m == -1) {
                        repeat = Integer.parseInt(s.substring(0, s.indexOf("s")));
                    } else {
                        repeat = repeat+Integer.parseInt(s.substring(m+1, s.indexOf("s")));
                    }
                }
            } catch(Exception ignored) {

            }

        }
        this.chance = chance;
        this.repeat = repeat;
    }

    public void disable(UserPet upet) {
        Bukkit.getScheduler().cancelTask(task);
        desecute(upet);
    }


    private long step = 0;
    private int desecuteAfter = -1;

    @Getter
    private int task;

    public void execute(UserPet upet) {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Pet.getInstance(), () -> {
            if(Math.random()*100 > chance) return;
            step++;

            if(desecuteAfter != -1) {
                if(step%desecuteAfter == 0) {
                    desecute(upet);
                    return;
                }
            }
            p = upet.getOwner();

            //Player p = upet.getOwner();
            switch(a) {
                case EXP:
                    p.setTotalExperience(p.getTotalExperience()+getArg(1));
                    break;
                case FLY:
                    p.setAllowFlight(true);
                    break;
                default:
                    break;
                case TEMP_FLY:
                    if(desecuteAfter == -1) desecuteAfter = getArg(1);
                    p.setAllowFlight(true);
                    break;
                case CURE:
                    p.removePotionEffect(PotionEffectType.getByName(args[1]));
                    break;
                case DISARM_OPPONENT:
                    for(Entity e : p.getNearbyEntities(5, 5, 5)) {
                        if(e instanceof Player) {
                            ((Player)e).dropItem(true);
                        }
                    }
                    break;
                case DISARM_SELF:
                    p.dropItem(true);
                    break;
                case GOD:
                    p.setInvulnerable(true);
                case TEMP_GOD:
                    if(desecuteAfter == -1) desecuteAfter = getArg(1);
                    p.setInvulnerable(true);
                case TITLE:
                    p.sendTitle(Utils.color(args[1]), "", getArg(2), getArg(3), getArg(4));
                    break;
                case SUBTITLE:
                    p.sendTitle("", Utils.color(args[1]), getArg(2), getArg(3), getArg(4));
                    break;
                case MESSAGE:
                    p.sendMessage(Utils.color(args[1]));
                    break;
                case FIREBALL:
                    p.launchProjectile(Fireball.class);
                    break;
                case SHOOT_ARROW:
                    p.launchProjectile(Arrow.class);
                    break;
                case SHOOT_SNOWBALL:
                    p.launchProjectile(Snowball.class);
                    break;
                case PLAYER_COMMAND:
                    p.chat("/"+parsePs(args[1], upet));
                    break;
                case CONSOLE_COMMAND:
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsePs(args[1], upet));
                    break;
                case EXTINGUISH:
                    p.setFireTicks(0);
                    break;
                case SET_FIRE:
                    p.setFireTicks(getArg(1)*20);
                    break;
                case PUMPKIN:
                    if(desecuteAfter == -1) desecuteAfter = getArg(1);
                    setFakeSlot(p, EquipmentSlot.HEAD, new ItemStack(Material.PUMPKIN));
                    break;
                case INVISIBLE_ARMOR:
                    setFakeSlot(p, EquipmentSlot.HEAD, null);
                    setFakeSlot(p, EquipmentSlot.LEGS, null);
                    setFakeSlot(p, EquipmentSlot.FEET, null);
                    setFakeSlot(p, EquipmentSlot.CHEST, null);
                    break;
                case INVISIBLE_HAND:
                    setFakeSlot(p, EquipmentSlot.HAND, null);
                    break;
                case FAKE_ARMOR:
                    setFakeSlot(p, EquipmentSlot.valueOf(args[1]), new ItemStack(Material.valueOf(args[2])));
                    break;
                case ADD_FOOD:
                    p.setFoodLevel(p.getFoodLevel()+getArg(1));
                    break;
                case CONSOLE_LOG:
                    Bukkit.getLogger().info(args[1]);
                    break;
                case FAKE_LOCATION:
                    for(Player g : p.getWorld().getPlayers()) {
                        PacketContainer t = Pet.getInstance().getPacketUtils().teleportEntity(p.getEntityId(), p.getLocation().clone().add(Math.random()*20-10, Math.random()*20-10, Math.random()*20-10), true);
                        try {
                            ProtocolLibrary.getProtocolManager().sendServerPacket(g, t);
                        } catch(Exception ignored) {}
                    }
                    break;
                case SWAP_ITEMS:
                    for(int i = 0; i < 7; i++) {
                        ItemStack c = p.getInventory().getItem(i).clone();
                        double d = Math.random()*9;
                        try {
                            p.getInventory().setItem(i, p.getInventory().getItem((int)d).clone());
                            p.getInventory().setItem((int)d, c);
                        } catch(Exception ignored) {}
                    }
                    break;
                case LIGHNING_ON_PLAYER:
                    p.getWorld().strikeLightning(p.getLocation());
                    break;
                case LIGHTNING_LOOKING:
                    p.getWorld().strikeLightning(p.getEyeLocation());
                    break;
                case FREEZE:
                    if(desecuteAfter == -1) desecuteAfter = getArg(1);
                    p.setWalkSpeed(0);
                    p.spawnParticle(Particle.SNOWBALL, p.getLocation(), 10);
                    break;
                case VELOCITY:
                    p.setVelocity(new Vector(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3])));
                    break;
                case LAUNCH:
                    p.setVelocity(p.getVelocity().multiply(getArg(1)));
                    break;
                case NO_KNOCKBACK:
                    Bukkit.getPluginManager().registerEvents(this, Pet.getInstance());
                    break;
                case NO_FALL_DAMAGE:
                    Bukkit.getPluginManager().registerEvents(this, Pet.getInstance());
                    break;
                case PLAY_SOUND:
                    p.playSound(p.getLocation(), Sound.valueOf(args[1]), getArg(2), getArg(3));
                    break;
                case PLAYER_PARTICLE:
                    p.spawnParticle(Particle.valueOf(args[1]), p.getLocation(), getArg(2));
                    break;
                case PET_PARTICLE:
                    p.spawnParticle(Particle.valueOf(args[1]), upet.getTheoricalLocation(), getArg(2));
                    break;
                case INVISIBLE:
                    p.setInvisible(true);
                    break;
                case PLAY_SOUND_EVERYONE:
                    for(Player g : p.getWorld().getPlayers()) {
                        g.playSound(p.getLocation(), Sound.valueOf(args[1]), getArg(2), getArg(3));
                    }
                    break;
                case POISON_NEAR:
                    for(Entity e : p.getNearbyEntities(5, 5, 5)) {
                        if(e instanceof Player) {
                            ((Player)e).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 1, 30));
                        }
                    }
                    break;
                case HEAL:
                    p.setHealth(p.getMaxHealth());
                    break;
                case SHOOT_ENDERPEARL:
                    p.launchProjectile(EnderPearl.class);
                    break;
                case ADD_HEALTH:
                    p.setMaxHealth(getArg(1));
                    break;
                case DAMAGE:
                    p.damage(getArg(1));
                    break;
                case INCREASE_DAMAGE:
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, repeat*20+2, 1));
                    break;
                case POTION:
                    p.addPotionEffect(new PotionEffect(PotionEffectType.getByName(args[1]), getArg(2)*20, getArg(3)));
                    break;
                case FAKE_HAND:
                    setFakeSlot(p, EquipmentSlot.HAND, new ItemStack(Material.valueOf(args[1])));
                    break;
                case EXPLOSION:
                    p.getWorld().createExplosion(p.getLocation(), Float.parseFloat(args[1]), Boolean.parseBoolean(args[2]), Boolean.parseBoolean(args[2]));
                    break;
            }
        }, 1, repeat*20);
    }

    private Player p;

    @EventHandler
    void onVelocity(PlayerVelocityEvent event) {
        if(a != AbilityType.NO_KNOCKBACK) return;
        if(!event.getPlayer().equals(p)) return;
        event.setCancelled(true);
    }

    @EventHandler
    void onDamage(EntityDamageEvent event) {
        if(a != AbilityType.NO_FALL_DAMAGE) return;
        if(!event.getEntity().getUniqueId().equals(p.getUniqueId())) return;
        if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) event.setCancelled(true);
    }

    public void desecute(UserPet upet) {
        Player p = upet.getOwner();
        switch(a) {
            case FAKE_LOCATION:
                for(Player g : p.getWorld().getPlayers()) {
                    PacketContainer t = Pet.getInstance().getPacketUtils().teleportEntity(p.getEntityId(), p.getLocation(), true);
                    try {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(g, t);
                    } catch(Exception ignored) {}
                }
                break;
            case FAKE_HAND:
                resetSlot(p, EquipmentSlot.HAND);
                break;
            case POTION:
                p.removePotionEffect(PotionEffectType.getByName(args[1]));
                break;
            case ADD_HEALTH:
                p.resetMaxHealth();
                break;
            case INVISIBLE:
                p.setInvisible(false);
                break;
            case NO_FALL_DAMAGE:
                EntityDamageEvent.getHandlerList().unregister(this);
                break;
            case NO_KNOCKBACK:
                PlayerVelocityEvent.getHandlerList().unregister(this);
                break;
            case FREEZE:
                p.setWalkSpeed(0.2f);
                break;
            case TEMP_FLY:
                p.setAllowFlight(false);
                break;
            case FLY:
                p.setAllowFlight(false);
            default:
                break;
            case GOD:
                p.setInvulnerable(false);
                break;
            case TEMP_GOD:
                p.setInvulnerable(false);
                break;
            case SET_FIRE:
                p.setFireTicks(0);
                break;
            case PUMPKIN:
                resetSlot(p, EquipmentSlot.HEAD);
                break;
            case INVISIBLE_ARMOR:
                resetSlot(p, EquipmentSlot.CHEST);
                resetSlot(p, EquipmentSlot.FEET);
                resetSlot(p, EquipmentSlot.LEGS);
                resetSlot(p, EquipmentSlot.HEAD);
                break;
            case INVISIBLE_HAND:
                resetSlot(p, EquipmentSlot.HAND);
                break;
            case FAKE_ARMOR:
                resetSlot(p, EquipmentSlot.valueOf(args[1]));
                break;
        }
    }

    private String parsePs(String s, UserPet upet) {
        return s.replace("%player%", upet.getOwner().getName()).replace("%pet%", upet.getType().getName());
    }

    private void setFakeSlot(Player p, EquipmentSlot slot, ItemStack stack) {
        Pet.getPackUtils().executePacket(Pet.getPackUtils().equipItem(p.getEntityId(), fromEquipment(slot), stack), p.getWorld());
    }
    private EnumWrappers.ItemSlot fromEquipment(EquipmentSlot s) {
        switch(s) {
            default:
                return EnumWrappers.ItemSlot.valueOf(s.name());
            case HAND:
                return EnumWrappers.ItemSlot.MAINHAND;
            case OFF_HAND:
                return EnumWrappers.ItemSlot.OFFHAND;
        }
    }

    private void resetSlot(Player p, EquipmentSlot slot) {
        setFakeSlot(p, slot, p.getInventory().getItem(slot));
    }

    private int getArg(int i) {
        try {
            return Integer.parseInt(args[i]);
        } catch(Exception ignored) {
            return 1;
        }
    }

}
