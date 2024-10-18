/*
 * This file is part of HPET - Packet Based Pet Plugin
 *
 * TOS (Terms of Service)
 * You are not allowed to decompile, or redestribuite part of this code if not authorized by the original author.
 * You are not allowed to claim this resource as yours.
 */
package it.heron.hpet.commands;

import it.heron.hpet.main.guis.GUI;
import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.main.Utils;
import it.heron.hpet.pettypes.PetType;
import it.heron.hpet.userpets.childpet.ChildPet;
import it.heron.hpet.userpets.UserPet;
import net.kyori.adventure.text.format.TextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import it.heron.hpet.animation.PetParticle;
import it.heron.hpet.groups.Group;
import it.heron.hpet.messages.Messages;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p;
        if(sender instanceof Player) {
            p = (Player)sender;
        } else {
            try {
                p = Bukkit.getPlayer(args[args.length-1]);
            } catch(Exception ignored) {
                p = null;
            }
        }
        String argument = null;
        if(args.length > 0) {
            argument = args[0].toLowerCase();
        }

        if(args.length >= 2) {
            PetType type = PetPlugin.getPetTypeByName(args[1]);
            if(type == null && PetPlugin.getApi().hasUserPet(p)) type = PetPlugin.getApi().getUserPet(p).getType();
            if(parseCommand(p, argument, "setlevel", false, "pet.setlevel", type)) {
                int amount = 1;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch(Exception e) {
                    p.sendMessage("§cInvalid number!");
                    return false;
                }
                PetPlugin.getInstance().getDatabase().setPetLevel(p.getUniqueId(), type, amount);
                p.sendMessage("§eNow "+type.getName()+" is level "+amount+"!");
                return true;
            }
            if(parseCommand(p, argument, "rename", true, "pet.rename")) {
                UserPet upet = PetPlugin.getApi().getUserPet(p);
                String s = args[1];
                if(p.hasPermission("pet.rename.color")) s = Utils.color(args[1]);
                if(s.length() > PetPlugin.getInstance().getConfig().getInt("nametags.maxlength")) s = s.substring(0, PetPlugin.getInstance().getConfig().getInt("nametags.maxlength"));
                for(String g : PetPlugin.getInstance().getConfig().getStringList("nametags.invalidnames")) {
                    if(s.contains(g)) s = s.replace(g, "*");
                }
                upet.setName(PetPlugin.getInstance().getNameFormat().replace("%name%",s).replace("%level%",upet.getLevel()+"").replace("%player%",p.getName()));
                upet.update();
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                p.playEffect(p.getLocation(), Effect.SMOKE, 1);
                return true;
            }
            if(parseCommand(p, argument, "addlevel", false, "pet.addlevel", type)) {
                int amount = 1;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch(Exception e) {
                    p.sendMessage("§cInvalid number!");
                    return false;
                }
                int current = 0;
                try {
                    current = PetPlugin.getInstance().getDatabase().getPetLevel(p.getUniqueId(), type);
                } catch (Exception ignored) {}
                PetPlugin.getInstance().getDatabase().setPetLevel(p.getUniqueId(), type, amount+current);
                p.sendMessage("§eNow "+type.getName()+" is level "+(current+amount)+"!");
                return true;
            }
            if(parseCommand(p, argument, "removelevel", false, "pet.addlevel", type)) {
                int amount = 1;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch(Exception e) {
                    p.sendMessage("§cInvalid number!");
                    return false;
                }
                int current = 0;
                try {
                    current = PetPlugin.getInstance().getDatabase().getPetLevel(p.getUniqueId(), type);
                } catch (Exception ignored) {}
                PetPlugin.getInstance().getDatabase().setPetLevel(p.getUniqueId(), type, current-amount);
                p.sendMessage("§eNow "+type.getName()+" is level "+(current-amount)+"!");
                return true;
            }
            if(parseCommand(p,argument,"color",true,"pet.color")) {
                Color color = null;
                try {
                    TextColor c = TextColor.fromHexString(args[1]);
                    color = Color.fromRGB(c.red(),c.green(),c.blue());
                } catch (Exception ignored) {
                    p.sendMessage(Messages.getMessage("pet.color.invalid"));
                    return false;
                }
                UserPet pet = PetPlugin.getApi().getUserPet(p);
                pet.setColor(color);
                pet.update();
                p.sendMessage(Messages.getMessage("pet.color.change"));
                return true;
            }
            if(parseCommand(p, argument, "select", false, null, type)) {
                if(!sender.hasPermission("pet.use."+type.getName())) {
                    p.sendMessage(Messages.getMessage("error.noperm.use"));
                    return false;
                }
                PetPlugin.getApi().selectPet(p, type);
                return true;
            }
            if(parseCommand(p, argument, "buy", false, null, PetPlugin.getInstance().getEconomy(), type)) {
                if(p.hasPermission("pet.use."+type.getName())) {
                    p.sendMessage(Messages.getMessage("error.alreadybought"));
                    return false;
                }
                if(type.getPrice() == null) {
                    p.sendMessage(Messages.getMessage("pet.buy.noprice"));
                    return false;
                }
                Economy econ = PetPlugin.getInstance().getEconomy();
                double bal = econ.getBalance(p);
                if(bal < type.getPrice()) {
                    p.sendMessage(Messages.getMessage("pet.buy.notenough"));
                    return false;
                }
                econ.withdrawPlayer(p, type.getPrice());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Messages.getMessage("pet.buy.permissioncommand").replace("%player%", p.getName()).replace("%petname%", type.getName()));
                p.sendMessage(Messages.getMessage("pet.buy.bought"));
                PetPlugin.getApi().selectPet(p, type);
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
                UserPet pet = PetPlugin.getApi().getUserPet(p);
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
                if(PetPlugin.getInstance().isDemo()) {
                    demo = "§d§lDEMO EDITION";
                }
                p.sendMessage("§a§lHPET: §eversion "+ PetPlugin.getInstance().getDescription().getVersion()+" "+demo);
                return true;
            }
            if(parseCommand(p, argument, "level", true, "pet.level")) {
                p.sendMessage(Messages.getMessage("level")+ PetPlugin.getApi().getUserPet(p).getLevel());
                return true;
            }
            if(parseCommand(p, argument, "update", true, "pet.update")) {
                for(UserPet userPet : PetPlugin.getApi().getUserPets(p)) {
                    userPet.update();
                }
                p.sendMessage(Messages.getMessage("pet.respawn"));
                return true;
            }
            if(parseCommand(p, argument, "remove", true, "pet.remove")) {
                for(UserPet userPet : PetPlugin.getApi().getUserPets(p)) {
                    userPet.remove();
                }
                p.sendMessage(Messages.getMessage("pet.remove"));
                return true;
            }
            if(parseCommand(p, argument, "trail", true, "pet.trail")) {
                UserPet pet = PetPlugin.getApi().getUserPet(p);
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
                UserPet pet = PetPlugin.getApi().getUserPet(p);
                pet.setParticle(null);
                p.sendMessage(Messages.getMessage("pet.particle.remove"));
                return true;
            }
            if(parseCommand(p, argument, "glow", true, "pet.glow")) {
                UserPet pet = PetPlugin.getApi().getUserPet(p);
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
                PetPlugin.getInstance().reload();
                sender.sendMessage("§aHPET reloaded!");
                return true;
            }
        }


        int page = 0;
        if(args.length == 1) {
            try {
                page = Integer.parseInt(argument);
            } catch(Exception e) {
                return true;
            }
        }
        if(!PetPlugin.getInstance().getConfig().getBoolean("enableGui", true)) {
            p.sendMessage("§cGui is disabled");
            return false;
        }
        Inventory inv = GUI.getGUI(p);

        p.openInventory(inv);
        PetPlugin.getInstance().setOpenedPage(p, page);
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

    public boolean parseCommand(CommandSender sender, String argument, String validArgument, boolean requirePet, String permission) {
        return parseCommand(sender, argument, validArgument, requirePet, permission, true);
    }
    public boolean parseCommand(CommandSender sender, String argument, String validArgument, boolean requirePet, String permission, Object... activate) {
        if(argument != null && !argument.equals(validArgument)) return false;
        if(sender != null && permission != null && !sender.hasPermission(permission)) {sender.sendMessage(Messages.getMessage("error.noperm.command")); return false;}
        if(requirePet && !PetPlugin.getApi().hasUserPet((Player)sender)) {sender.sendMessage(Messages.getMessage("error.nosel")); return false;}
        for(Object o : activate) {
            if(o == null) {sender.sendMessage(Messages.getMessage("error.invalid")); return false;}
        }
        return true;
    }


}
