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

    /**
     * Constructs a MessagesHandler with the specified plugin instance and initializes MiniMessage for message formatting.
     *
     * @param plugin the JavaPlugin instance associated with this handler
     */
    public MessagesHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
    }

    /**
     * Returns the name of this module.
     *
     * @return the string "Messages"
     */
    @Override
    public String name() {
        return "Messages";
    }

    /**
     * Loads the Messages module and attempts to acquire a PapiModule instance from the server's service manager for placeholder support.
     *
     * Marks the module as loaded.
     */
    @Override
    public void load() {
        Module papi = plugin.getServer().getServicesManager().load(Module.class);
        if (papi instanceof PapiModule) {
            this.papiModule = (PapiModule) papi;
        }
        loaded = true;
    }

    /**
     * Marks the module as unloaded by setting its loaded state to false.
     */
    @Override
    public void unload() {
        loaded = false;
    }

    /**
     * Indicates whether the module is currently loaded.
     *
     * @return {@code true} if the module is loaded; {@code false} otherwise
     */
    @Override
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Sends a formatted MiniMessage string to the specified player.
     *
     * The message is processed to replace internal and PlaceholderAPI placeholders before being sent.
     *
     * @param player the player to receive the message
     * @param message the MiniMessage-formatted string to send
     */
    public void sendMessage(Player player, String message) {
        String formatted = formatMessage(player, message);
        plugin.getServer().getPlayer(player.getUniqueId()).sendMessage(miniMessage.deserialize(formatted));
    }

    /**
     * Returns the provided message string without modification.
     *
     * @param message the message to return
     * @return the input message string unchanged
     */
    public String getRawString(String message) {
        return message; // In real implementation, this would fetch from config
    }

    /**
     * Formats a message string for a player by replacing internal and PlaceholderAPI placeholders.
     *
     * @param player the player for whom the message is being formatted
     * @param message the message string containing placeholders
     * @return the formatted message string with all applicable placeholders replaced
     */
    private String formatMessage(Player player, String message) {
        // Replace internal placeholders
        message = replaceInternalPlaceholders(message, player);
        
        // Replace PAPI placeholders if available
        if (papiModule != null) {
            message = papiModule.parsePlaceholders(player, message);
        }
        
        return message;
    }

    /**
     * Replaces internal placeholders in the message string with player-specific values.
     *
     * Currently replaces the {player} placeholder with the player's name. Additional placeholder logic can be added as needed.
     *
     * @param message the message string containing placeholders
     * @param player the player whose information is used for replacement
     * @return the message with internal placeholders replaced
     */
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
