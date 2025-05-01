package it.heron.hpet.modules.hooks;

import it.heron.hpet.modules.abstracts.PluginHook;
import org.bukkit.plugin.java.JavaPlugin;
import it.heron.hpet.placeholders.PlaceholdersExtension;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

public class PapiModule extends PluginHook {

    private PlaceholdersExtension extension;

    /**
     * Constructs a new PapiModule with the specified plugin instance.
     *
     * @param plugin the JavaPlugin instance to associate with this module
     */
    public PapiModule(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Returns the name of the plugin hook, identifying it as "PlaceholderAPI".
     *
     * @return the string "PlaceholderAPI"
     */
    @Override
    public String name() {
        return "PlaceholderAPI";
    }

    public String parsePlaceholders(OfflinePlayer player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @Override
    protected void onLoad() {
        this.extension = new PlaceholdersExtension();
        extension.register();
    }

    @Override
    protected void onUnload() {
        extension.unregister();
    }
}
