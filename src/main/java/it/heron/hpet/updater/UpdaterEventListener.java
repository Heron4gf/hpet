package it.heron.hpet.updater;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UpdaterEventListener implements Listener {
    private static Set<UUID> toNotify = new HashSet<>();

    @EventHandler
    void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        if(!player.hasPermission("pet.autoupdater")) return;
        if(AutoUpdater.isThisLatestUpdate()) return;
        toNotify.add(player.getUniqueId());
    }

    @EventHandler
    void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(!toNotify.contains(player.getUniqueId())) return;
        toNotify.remove(player.getUniqueId());

        String currentVersion = AutoUpdater.currentVersion(); // Ensure this is public or accessible
        String latestVersion = AutoUpdater.latestVersion();   // Ensure this is public or accessible
        String updateLink = AutoUpdater.UPDATE_LINK;

        // Construct the first part of the message (the static text)
        TextComponent mainMessage = new TextComponent();
        mainMessage.setText(ChatColor.RED + "" + ChatColor.BOLD + "HPET UPDATER: "
                + ChatColor.GRAY + "You're running an old version of HPET (" + currentVersion + "). ");

        // Create the clickable link part
        TextComponent linkMessage = new TextComponent();
        linkMessage.setText(ChatColor.AQUA + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "[DOWNLOAD " + latestVersion + " UPDATE]");
        linkMessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, updateLink));

        // Combine both messages
        mainMessage.addExtra(linkMessage);

        // Send the message to the player
        player.spigot().sendMessage(mainMessage);
    }
}
