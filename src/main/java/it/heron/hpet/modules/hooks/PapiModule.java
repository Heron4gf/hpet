package it.heron.hpet.modules.hooks;

import it.heron.hpet.modules.abstracts.PluginHook;
import it.heron.hpet.placeholders.PlaceholdersExtension;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

public class PapiModule extends PluginHook {

    private PlaceholdersExtension extension;

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
