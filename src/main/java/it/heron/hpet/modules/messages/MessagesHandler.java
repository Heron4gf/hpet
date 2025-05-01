package it.heron.hpet.modules.messages;

import it.heron.hpet.modules.abstracts.Module;
import it.heron.hpet.modules.hooks.PapiModule;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagesHandler implements Module {
    private final JavaPlugin plugin;
    private boolean loaded = false;
    private MiniMessage miniMessage;
    private PapiModule papiModule;

    public MessagesHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public String name() {
        return "Messages";
    }

    @Override
    public void load() {
        Module papi = plugin.getServer().getServicesManager().load(Module.class);
        if (papi instanceof PapiModule) {
            this.papiModule = (PapiModule) papi;
        }
        loaded = true;
    }

    @Override
    public void unload() {
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    public void sendMessage(Player player, String message) {
        String formatted = formatMessage(player, message);
        plugin.getServer().getPlayer(player.getUniqueId()).sendMessage(miniMessage.deserialize(formatted));
    }

    public String getRawString(String message) {
        return message; // In real implementation, this would fetch from config
    }

    private String formatMessage(Player player, String message) {
        // Replace internal placeholders
        message = replaceInternalPlaceholders(message, player);
        
        // Replace PAPI placeholders if available
        if (papiModule != null) {
            message = papiModule.parsePlaceholders(player, message);
        }
        
        return message;
    }

    private String replaceInternalPlaceholders(String message, Player player) {
        // Example: Replace {player} with player name
        message = message.replace("{player}", player.getName());
        
        // Add more internal placeholder replacements as needed
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(message);
        
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            // Add custom placeholder logic here
        }
        
        return message;
    }
}
