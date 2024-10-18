/*
 * This file is part of HPET - Packet Based Pet Plugin
 *
 * TOS (Terms of Service)
 * You are not allowed to decompile, or redestribuite part of this code if not authorized by the original author.
 * You are not allowed to claim this resource as yours.
 */
package it.heron.hpet.events;

import com.comphenix.protocol.ProtocolLibrary;
import it.heron.hpet.main.guis.GUI;
import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.main.Utils;
import it.heron.hpet.messages.Messages;
import it.heron.hpet.pettypes.PetType;
import it.heron.hpet.userpets.UserPet;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
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
        if(PetPlugin.getInstance().isUsingLegacyId()) {
            return;
        }
        if(!(event.getEntity() instanceof Trident)) {
            return;
        }
        if(!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getEntity().getShooter();
        UserPet pet = PetPlugin.getApi().getUserPet(p);
        if(pet == null) {
            return;
        }
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(p, PetPlugin.getInstance().getPacketUtils().destroyEntity(pet.getChild().getId()));
            if(pet.getChild() != null) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(p, PetPlugin.getInstance().getPacketUtils().destroyEntity(pet.getChild().getId()));
            }
        } catch(Exception e) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                pet.update();
            }
        }.runTaskLater(PetPlugin.getInstance(), 3);
    }

    @EventHandler
    void onDeath(PlayerDeathEvent event) {
        UserPet upet = PetPlugin.getApi().getUserPet(event.getEntity());
        if(upet != null) upet.despawn();
    }

    @EventHandler
    void onTP(PlayerTeleportEvent event) {
        if(PetPlugin.getInstance().getConfig().getInt("delay.teleport") < 0) return;
        Player player = event.getPlayer();
        try {
            if(event.getFrom().distance(event.getTo()) < 10) {
                return;
            }
        } catch (Exception ignored) {}

        new BukkitRunnable() {
            @Override
            public void run() {
                List<UserPet> userPets = PetPlugin.getApi().getUserPets(player);
                if(userPets != null && !userPets.isEmpty()) {
                    for(UserPet userPet : userPets) {
                        userPet.update();
                    }
                }
            }
        }.runTaskLater(PetPlugin.getInstance(), PetPlugin.getInstance().getConfig().getInt("delay.teleport"));
    }


    @EventHandler
    void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        List<UserPet> userPets = PetPlugin.getApi().getUserPets(player);
        if(userPets != null && !userPets.isEmpty()) {
            for(UserPet userPet : userPets) {
                userPet.despawn(event.getFrom());
            }
        }
        if(PetPlugin.getInstance().getConfig().getInt("delay.world_change") < 0) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                List<UserPet> userPets = PetPlugin.getApi().getUserPets(player);
                if(userPets != null && !userPets.isEmpty()) {
                    for(UserPet userPet : userPets) {
                        userPet.update();
                    }
                }
                Utils.loadVisiblePets(player);
            }
        }.runTaskLater(PetPlugin.getInstance(), PetPlugin.getInstance().getConfig().getInt("delay.world_change"));
    }

    @EventHandler
    void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PetPlugin.getInstance().removeFromOpenedPage(player);
        List<UserPet> userPets = PetPlugin.getApi().getUserPets(player);
        if(userPets != null && !userPets.isEmpty()) {
            try {
                Utils.savePets(player,userPets);
            } finally {
                for(UserPet userPet : userPets) {
                    userPet.remove();
                }
            }
        } else {
            Utils.savePets(player,null);
        }
    }

    @EventHandler
    void onRespawn(PlayerRespawnEvent event) {
        if(PetPlugin.getInstance().getConfig().getInt("delay.respawn") < 0) return;
        Player p = event.getPlayer();
        if(event.getRespawnLocation().getWorld().equals(p.getWorld()) && PetPlugin.getApi().hasUserPet(p)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    List<UserPet> userPets = PetPlugin.getApi().getUserPets(p);
                    if(userPets != null && !userPets.isEmpty()) {
                        for(UserPet userPet : userPets) {
                            userPet.update();
                        }
                    }
                }
            }.runTaskLater(PetPlugin.getInstance(), PetPlugin.getInstance().getConfig().getInt("delay.respawn"));
        }
    }

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        if(PetPlugin.getInstance().getConfig().getInt("delay.join") < 0) return;
        Player p = event.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                Utils.loadVisiblePets(p);
                Utils.loadDatabasePet(p);
            }
        }.runTaskLater(PetPlugin.getInstance(), PetPlugin.getInstance().getConfig().getInt("delay.join"));
    }

    @EventHandler
    void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        if(event.isCancelled()) return;
        if(!PetPlugin.getInstance().getConfig().getBoolean("useAliases")) return;
        for(String s : PetPlugin.getInstance().getConfig().getStringList("alias")) {
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
        if(PetPlugin.getInstance().isUsingLegacySound()) {
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
                c = (PetPlugin.getInstance().getOpenedPage().get(p.getUniqueId())+1)+"";
                break;
        }
        // 51 remove
        // 49 rename
        // 47 respawn
        // 48 trail
        // 50 glow
        if(!c.equals("")) {
            p.closeInventory();
            p.chat("/hpet "+c);
            return;
        }
        int page = PetPlugin.getInstance().getOpenedPage().get(p.getUniqueId());
        if(slot == 27) {
            if(page >= 10) {
                p.chat("/hpet");
            } else {
                p.chat("/hpet "+(PetPlugin.getInstance().getOpenedPage().get(p.getUniqueId())-1));
            }
            return;
        }
        List<HSlot> canSee = null;

        if(page >= 10) {
            try {
                canSee = Arrays.asList(((Group) PetPlugin.getInstance().getPetTypes().get(page - 10)).getType());
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
                PetPlugin.getApi().selectPet(p, type);
            } else {
                if(type.getPrice() != null) p.chat("/hpet buy "+type.getName());
            }
        } else {
            p.chat("/hpet "+(slot+10));
            return;
        }
        p.closeInventory();
    }
}
