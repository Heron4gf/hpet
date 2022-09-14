/*
 * This file is part of HPET - Packet Based Pet Plugin
 *
 * TOS (Terms of Service)
 * You are not allowed to decompile, or redestribuite part of this code if not authorized by the original author.
 * You are not allowed to claim this resource as yours.
 */
package it.heron.hpet;

import it.heron.hpet.pettypes.PetType;
import it.heron.hpet.userpets.UserPet;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import it.heron.hpet.animation.PetParticle;
import it.heron.hpet.groups.Group;
import it.heron.hpet.messages.Messages;

import java.util.ArrayList;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p;
        if(sender instanceof Player) {
            p = (Player)sender;
        } else {
            p = Bukkit.getPlayer(args[args.length-1]);
        }
        String argument = null;
        if(args.length > 0) {
            argument = args[0].toLowerCase();
        }

        if(args.length >= 2) {
            PetType type = Pet.getPetTypeByName(args[1]);
            if(parseCommand(p, argument, "setlevel", false, "pet.setlevel", type)) {
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch(Exception e) {
                    p.sendMessage("§cInvalid number!");
                    return false;
                }
                Pet.getApi().setPetLevel(p, type.getName(), amount);
                p.sendMessage("§eNow "+type.getName()+" is level "+amount+"!");
                return true;
            }
            if(parseCommand(p, argument, "rename", true, "pet.rename")) {
                UserPet upet = Pet.getApi().getUserPet(p);
                String s = args[1];
                if(p.hasPermission("pet.rename.color")) s = Utils.color(args[1]);
                if(s.length() > Pet.getInstance().getConfig().getInt("nametags.maxlength")) s = s.substring(0, Pet.getInstance().getConfig().getInt("nametags.maxlength"));
                for(String g : Pet.getInstance().getConfig().getStringList("nametags.invalidnames")) {
                    if(s.contains(g)) s = s.replace(g, "*");
                }
                upet.setName(s);
                upet.update();
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                p.playEffect(p.getLocation(), Effect.SMOKE, 1);
                return true;
            }
            if(parseCommand(p, argument, "addlevel", false, "pet.addlevel", type)) {
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch(Exception e) {
                    p.sendMessage("§cInvalid number!");
                    return false;
                }
                int current = 0;
                try {
                    current = Pet.getApi().getPetLevel(p, type.getName());
                } catch (Exception ignored) {}
                Pet.getApi().setPetLevel(p, type.getName(), current+amount);
                p.sendMessage("§eNow "+type.getName()+" is level "+(current+amount)+"!");
                return true;
            }
            if(parseCommand(p, argument, "removelevel", false, "pet.addlevel", type)) {
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch(Exception e) {
                    p.sendMessage("§cInvalid number!");
                    return false;
                }
                int current = 0;
                try {
                    current = Pet.getApi().getPetLevel(p, type.getName());
                } catch (Exception ignored) {}
                Pet.getApi().setPetLevel(p, type.getName(), current-amount);
                p.sendMessage("§eNow "+type.getName()+" is level "+(current-amount)+"!");
                return true;
            }
            if(parseCommand(p, argument, "select", false, null, type)) {
                if(!sender.hasPermission("pet.use."+type.getName()) || Pet.getInstance().getDisabledWorlds().contains(p.getWorld())) {
                    p.sendMessage(Messages.getMessage("error.noperm.use"));
                    return false;
                }
                Pet.getApi().selectPet(p, type);
                return true;
            }
            if(parseCommand(p, argument, "buy", false, null, Pet.getInstance().getEconomy(), type)) {
                if(p.hasPermission("pet.use."+type.getName())) {
                    p.sendMessage(Messages.getMessage("error.alreadybought"));
                    return false;
                }
                if(type.getPrice() == null) {
                    p.sendMessage(Messages.getMessage("pet.buy.noprice"));
                    return false;
                }
                Economy econ = Pet.getInstance().getEconomy();
                double bal = econ.getBalance(p);
                if(bal < type.getPrice()) {
                    p.sendMessage(Messages.getMessage("pet.buy.notenough"));
                    return false;
                }
                econ.withdrawPlayer(p, type.getPrice());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Messages.getMessage("pet.buy.permissioncommand").replace("%player%", p.getName()).replace("%petname%", type.getName()));
                p.sendMessage(Messages.getMessage("pet.buy.bought"));
                Pet.getApi().selectPet(p, type);
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                return true;
            }
            if(parseCommand(p, argument, "particle", true, null)) {
                Particle particle;
                try {
                    particle = Particle.valueOf(args[1].toUpperCase());
                } catch(Exception e) {
                    p.sendMessage(Messages.getMessage("pet.particle.invalid"));
                    return false;
                }
                if(!p.hasPermission("pet.particle."+particle.name().toLowerCase())) {
                    p.sendMessage(Messages.getMessage("error.noperm.particle"));
                    return false;
                }
                UserPet pet = Pet.getApi().getUserPet(p);
                PetParticle petparticle = new PetParticle(particle);
                pet.setParticle(petparticle);
                p.sendMessage(Messages.getMessage("pet.particle.add"));
                return true;
            }
        }
        if(args.length >= 1) {
            if(parseCommand(p, argument, "rename", false, "pet.rename")) {
                p.sendMessage(Messages.getMessage("pet.rename.help"));
                return true;
            }
            if(parseCommand(p, argument, "version", false, "pet.version")) {
                String demo = "";
                if(Pet.getInstance().isDemo()) {
                    demo = "§d§lDEMO EDITION";
                }
                p.sendMessage("§a§lHPET: §eversion "+Pet.getInstance().getDescription().getVersion()+" "+demo);
                return true;
            }
            if(parseCommand(p, argument, "level", true, "pet.level")) {
                p.sendMessage(Messages.getMessage("level")+ Pet.getApi().getPetLevel(p, Pet.getApi().getUserPet(p).getType().getName()));
                return true;
            }
            if(parseCommand(p, argument, "update", true, "pet.update")) {
                Pet.getApi().getUserPet(p).update();
                p.sendMessage(Messages.getMessage("pet.respawn"));
                return true;
            }
            if(parseCommand(p, argument, "remove", true, "pet.remove")) {
                Pet.getApi().getUserPet(p).remove();
                p.sendMessage(Messages.getMessage("pet.remove"));
                return true;
            }
            if(parseCommand(p, argument, "trail", true, "pet.trail")) {
                UserPet pet = Pet.getApi().getUserPet(p);
                if(pet.getChild() == null) {
                    pet.setChild(new ChildPet());
                    p.sendMessage(Messages.getMessage("pet.trail.add"));
                } else {
                    pet.destroyChild();
                    p.sendMessage(Messages.getMessage("pet.trail.remove"));
                }
                pet.despawn();
                pet.update();
                return true;
            }
            if(parseCommand(p, argument, "particle", true, "pet.particle")) {
                UserPet pet = Pet.getApi().getUserPet(p);
                pet.setParticle(null);
                p.sendMessage(Messages.getMessage("pet.particle.remove"));
                return true;
            }
            if(parseCommand(p, argument, "glow", true, "pet.glow")) {
                UserPet pet = Pet.getApi().getUserPet(p);
                if(pet.isGlow()) {
                    pet.setGlow(false);
                    p.sendMessage(Messages.getMessage("pet.glow.remove"));
                } else {
                    pet.setGlow(true);
                    p.sendMessage(Messages.getMessage("pet.glow.add"));
                }
                pet.update();
                return true;
            }
            if(parseCommand(p, argument, "reload", false, "pet.reload")) {
                if(Pet.getInstance().isDemo()) {
                    p.sendMessage("§eReload is not supported in DEMO edition, buy HPET on SpigotMC!");
                    return false;
                }
                
                Pet.getInstance().reloadConfig();
                Messages.rl();
                Pet.getInstance().setPetConfiguration(YamlConfiguration.loadConfiguration(Pet.getInstance().getPetFile()));
                Pet.getInstance().setPetTypes(new ArrayList<>());
                Pet.getInstance().parsePetTypes();
                Pet.getInstance().clearCachedItems();
                try {
                    for(Plugin plugin : Pet.getInstance().getAddons()) {
                        try {
                            if(plugin != null) {
                                Pet.getInstance().getPluginLoader().disablePlugin(plugin);
                                Pet.getInstance().getPluginLoader().enablePlugin(plugin);
                                p.sendMessage("§aReloaded addon: "+plugin.getName());
                            }
                        } catch(Exception ignored) {
                            p.sendMessage("§cCould not reload addon: "+plugin.getName());
                        }
                    }
                } catch(Exception ignored) {}
                p.sendMessage("§aConfig reloaded!");
                return true;
            }
        }

        if(!Pet.getInstance().getConfig().getBoolean("enableGui", true)) {
            p.sendMessage("§cGui is disabled");
            return false;
        }

        int page = 0;
        if(args.length == 1) {
            try {
                page = Integer.parseInt(argument);
            } catch(Exception e) {
                p.sendMessage("§cInserted page number is not valid!");
            }
        }
        Inventory inv = GUI.getGUI(p);

        p.openInventory(inv);
        Pet.getInstance().setOpenedPage(p, page);
        if(page >= 10) {
            try {
                GUI.loadInventory(p, inv, ((Group)GUI.canSee(p).get(page-10)).getType());
            } catch(Exception ignored) {
                Bukkit.getLogger().info("Error loading the GUI!");
            }
        } else {
            GUI.loadInventory(p, inv, page);
        }
        return true;
    }

    public boolean parseCommand(Player p, String argument, String validArgument, boolean requirePet, String permission) {return parseCommand(p, argument, validArgument, requirePet, permission, true);}
    public boolean parseCommand(Player p, String argument, String validArgument, boolean requirePet, String permission, Object... activate) {
        if(argument != null && !argument.equals(validArgument)) return false;
        if(p != null && permission != null && !p.hasPermission(permission)) {p.sendMessage(Messages.getMessage("error.noperm.command")); return false;}
        if(requirePet && !Pet.getApi().hasUserPet(p)) {p.sendMessage(Messages.getMessage("error.nosel")); return false;}
        for(Object o : activate) {
            if(o == null) {p.sendMessage(Messages.getMessage("error.invalid")); return false;}
        }
        return true;
    }


}
