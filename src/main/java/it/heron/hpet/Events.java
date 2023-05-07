/*
 * This file is part of HPET - Packet Based Pet Plugin
 *
 * TOS (Terms of Service)
 * You are not allowed to decompile, or redestribuite part of this code if not authorized by the original author.
 * You are not allowed to claim this resource as yours.
 */
package it.heron.hpet;

import com.comphenix.protocol.ProtocolLibrary;
import it.heron.hpet.messages.Messages;
import it.heron.hpet.pettypes.PetType;
import it.heron.hpet.updater.Updater;
import it.heron.hpet.userpets.UserPet;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitRunnable;
import it.heron.hpet.groups.Group;
import it.heron.hpet.groups.HSlot;

import java.util.Arrays;
import java.util.List;

public class Events implements Listener {

    @EventHandler
    void onTridentThrow(ProjectileLaunchEvent event) {
        if(Pet.getInstance().isUsingLegacyId()) {
            return;
        }
        if(!(event.getEntity() instanceof Trident)) {
            return;
        }
        if(!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getEntity().getShooter();
        UserPet pet = Pet.getInstance().getPacketUtils().getPets().get(p.getUniqueId());
        if(pet == null) {
            return;
        }
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(p, Pet.getInstance().getPacketUtils().destroyEntity(pet.getChild().getId()));
            if(pet.getChild() != null) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(p, Pet.getInstance().getPacketUtils().destroyEntity(pet.getChild().getId()));
            }
        } catch(Exception e) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                pet.update();
            }
        }.runTaskLater(Pet.getInstance(), 3);
    }

    @EventHandler
    void onDeath(PlayerDeathEvent event) {
        UserPet upet = Pet.getApi().getUserPet(event.getEntity());
        if(upet != null) upet.despawn();
    }

    @EventHandler
    void onTP(PlayerTeleportEvent event) {
        UserPet pet = Pet.getApi().getUserPet(event.getPlayer());
        if(pet == null) return;
        pet.despawn(event.getFrom().getWorld());
        new BukkitRunnable() {
            @Override
            public void run() {
                pet.update();
            }
        }.runTaskLater(Pet.getInstance(), Pet.getInstance().getConfig().getInt("delay.teleport"));
    }


    @EventHandler
    void onWorldChange(PlayerChangedWorldEvent event) {
        Player p = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                Utils.loadVisiblePets(p);
            }
        }.runTaskLater(Pet.getInstance(), 5);
        UserPet upet = Pet.getApi().getUserPet(p);
        if(upet == null) return;
        if(Pet.getInstance().getDisabledWorlds().contains(p.getWorld().getUID())) {
            upet.remove();
            return;
        }
        upet.despawn(event.getFrom());
        new BukkitRunnable() {
            @Override
            public void run() {
                upet.update();
            }
        }.runTaskLater(Pet.getInstance(), Pet.getInstance().getConfig().getInt("delay.teleport"));
    }

    @EventHandler
    void onLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        UserPet pet = Pet.getApi().getUserPet(p);
        Pet.getInstance().removeFromOpenedPage(p);
        Utils.savePet(p, pet);
        if(pet != null) pet.remove();
    }

    @EventHandler
    void onRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        if(event.getRespawnLocation().getWorld().equals(p.getWorld()) && Pet.getApi().hasUserPet(p)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Pet.getApi().getUserPet(p).update();
                }
            }.runTaskLater(Pet.getInstance(),Pet.getInstance().getConfig().getInt("delay.teleport"));
        }
    }

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                Utils.loadVisiblePets(p);
                Utils.loadDatabasePet(p);
            }
        }.runTaskLater(Pet.getInstance(), Pet.getInstance().getConfig().getInt("delay.join"));

        if(p.hasPermission("pet.admin.notifications")) {
            Utils.runAsync(() -> {
                if(Updater.isThereUpdate()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.spigot().sendMessage(Utils.text("§eHey, there is a new §dHPET Update§e! §2[§a§lDOWNLOAD LATEST§2]", ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/hpet-1-8-1-18-packet-based-pet-system.93891/"));
                        }
                    }.runTaskLater(Pet.getInstance(), 30);
                }
            });
        }
    }

    @EventHandler
    void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        if(event.isCancelled()) return;
        if(!Pet.getInstance().getConfig().getBoolean("useAliases")) return;
        for(String s : Pet.getInstance().getConfig().getStringList("alias")) {
            if(event.getMessage().startsWith("/"+s) || event.getMessage().startsWith(s)) {
                event.setMessage(event.getMessage().replace(s, "hpet"));
                return;
            }
        }
    }

    @EventHandler
    void onClick(InventoryClickEvent event) {
        Player p = (Player)event.getWhoClicked();
        InventoryView view = event.getView();
        if(!view.getTitle().equals(Messages.getMessage("gui.name"))) {
            return;
        }
        event.setCancelled(true);
        if(Pet.getInstance().isUsingLegacySound()) {
            p.playSound(p.getLocation(), Sound.valueOf("CLICK"), 1, 1);
        } else {
            p.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
        }
        if(event.getCurrentItem() == null || event.getCurrentItem().isSimilar(Utils.getGUIStack("panel")) || event.getCurrentItem().isSimilar(Utils.getGUIStack("border"))) {
            return;
        }
        int slot = event.getSlot();
        String c = "";
        switch(slot) {
            case 51:
                c = "remove";
                break;
            case 47:
                c = "update";
                break;
            case 48:
                c = "trail";
                break;
            case 49:
                c = "rename";
                break;
            case 50:
                c = "glow";
                break;
            case 35:
                c = (Pet.getInstance().getOpenedPage().get(p.getUniqueId())+1)+"";
                break;
        }
        // 51 remove
        // 49 rename
        // 47 respawn
        // 48 trail
        // 50 glow
        if(!c.equals("")) {
            p.closeInventory();
            p.chat("/pet "+c);
            return;
        }
        int page = Pet.getInstance().getOpenedPage().get(p.getUniqueId());
        if(slot == 27) {
            if(page >= 10) {
                p.chat("/pet");
            } else {
                p.chat("/pet "+(Pet.getInstance().getOpenedPage().get(p.getUniqueId())-1));
            }
            return;
        }
        List<HSlot> canSee = null;

        if(page >= 10) {
            try {
                canSee = Arrays.asList(((Group) Pet.getInstance().getPetTypes().get(page - 10)).getType());
                page = 0;
            } catch(Exception ignored) {
                Bukkit.getLogger().info("Pet: Error parsing player pet!");
            }
        } else {
            canSee = GUI.canSee(p);
        }
        slot = GUI.getReverseSlot(slot);

        HSlot hslot = canSee.get(27*page+slot);
        if(hslot instanceof PetType) {
            PetType type = (PetType)hslot;
            if(p.hasPermission("pet.use."+type.getName())) {
                Pet.getApi().selectPet(p, type);
            } else {
                if(type.getPrice() != null) p.chat("/pet buy "+type.getName());
            }
        } else {
            p.chat("/pet "+(slot+10));
            return;
        }
        p.closeInventory();
    }
}
