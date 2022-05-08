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
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import it.heron.hpet.groups.HSlot;
import it.heron.hpet.messages.Messages;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GUI {

    public static void loadInventory(Player p, Inventory inv, int page) {

        new BukkitRunnable() {
            @Override
            public void run() {
                List<HSlot> canSee = GUI.canSee(p);
                int mult = page*27;
                if(page > 0) {
                    inv.setItem(27, Utils.getGUIStack("page.back"));
                }
                if(page < canSee.size()/27) {
                    inv.setItem(35, Utils.getGUIStack("page.next"));
                }
                for(int i = 0; i < 27; i++) {
                    HSlot slot;
                    try {
                        slot = canSee.get(mult+i);
                    } catch(Exception e) {
                        return;
                    }
                    if(slot != null) {
                        inv.setItem(getSlot(i), slot.getIcon(p));
                    }
                }
            }
        }.runTaskLater(Pet.getInstance(), 1);
    }
    public static void loadInventory(Player p, Inventory inv, PetType[] types) {
        for(int i = 0; i < 27; i++) {
            inv.setItem(getSlot(i), null);
        }
        for(int i = 0; i < types.length; i++) {
            PetType type = types[i];
            inv.setItem(getSlot(i), type.getIcon(p));
        }
        inv.setItem(27, Utils.getGUIStack("page.back"));
    }

    public static int getReverseSlot(int slot) {
        for(int i = 0; i < 27; i++) if(getSlot(i) == slot) return i;
        return 27;
    }

    // 10 - 16
    // 19 - 25
    // 28 - 34
    // 37 - 43

    private static int getSlot(int slot) {
        if(slot < 7) return slot+10;
        if(slot < 14) return slot+12;
        if(slot < 21) return slot+14;
        return slot+16;
    }

    private static Boolean enableItems = null;

    public static Inventory getGUI(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, Messages.getMessage("gui.name"));
        UUID u = p.getUniqueId();

        ItemStack panel = Utils.getGUIStack("panel");
        ItemStack border = Utils.getGUIStack("border");

        for(int i = 0; i < 2; i++) {
            inv.setItem(i, panel);
            inv.setItem(8-i, panel);
            inv.setItem(53-i, panel);
            inv.setItem(45+i, panel);
        }
        inv.setItem(9, panel);
        inv.setItem(17, panel);
        inv.setItem(44, panel);
        inv.setItem(36, panel);
        for(int i = 2; i < 7; i++) {
            inv.setItem(i, border);
        }
        inv.setItem(18, border);
        inv.setItem(26, border);
        inv.setItem(27, border);
        inv.setItem(35, border);
        if(enableItems == null) enableItems = YamlConfiguration.loadConfiguration(new File(Pet.getInstance().getDataFolder()+File.separator+"gui.yml")).getBoolean("gui.enablePetItems", true);
        if(Pet.getInstance().getPacketUtils().getPets().containsKey(u) && enableItems) {
            inv.setItem(51, Utils.getGUIStack("remove"));
            inv.setItem(49, Utils.getGUIStack("rename"));
            inv.setItem(47, Utils.getGUIStack("respawn"));

            UserPet pet = Pet.getApi().getUserPet(p);
            if(pet.getChild() == null) {
                inv.setItem(48, Utils.getGUIStack("trail.add"));
            } else {
                inv.setItem(48, Utils.getGUIStack("trail.remove"));
            }
            if(pet.isGlow()) {
                inv.setItem(50, Utils.getGUIStack("glow.remove"));
            } else {
                inv.setItem(50, Utils.getGUIStack("glow.add"));
            }
            /*Particle recommended = pet.getType().getRecommended();
            if(recommended != null) {
                if(pet.getParticle() == null) {
                    inv.setItem(31, Utils.getGUIStack("particle.add"));
                } else {
                    inv.setItem(31, Utils.getGUIStack("particle.remove"));
                }
            }*/
        }
        return inv;
    }

    public static List<HSlot> canSee(Player p) {
        List<HSlot> see = new ArrayList<>();
        for(HSlot slot : Pet.getInstance().getPetTypes()) {
            if(p.hasPermission("pet.see."+slot.getName())) {
                see.add(slot);
            }
        }
        return see;
    }

    public static List<String> petLore(List<String> list, PetType petType, Player p) {
        ArrayList<String> lore = new ArrayList<>();
        for(String s : list) {
            lore.add(s);
        }
        lore.add("");
        if(p == null) return lore;
        if(p.hasPermission("pet.use."+petType.getName())) {
            lore.add(Messages.getMessage("gui.select.can"));
        } else {
            if(petType.getPrice() != null) {
                lore.add(Messages.getMessage("pet.price")+petType.getPrice());
                lore.add("");
            }
            lore.add(Messages.getMessage("gui.select.cannot"));
        }
        return lore;
    }


}
