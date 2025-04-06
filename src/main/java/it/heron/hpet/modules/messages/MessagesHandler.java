package it.heron.hpet.modules.messages;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.abstracts.AbstractModule;
import it.heron.hpet.modules.hooks.PapiModule;
import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;
import lombok.NonNull;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;

public class MessagesHandler extends AbstractModule {


    private BukkitAudiences adventure;
    private File LOCALE_FILE;

    public @NonNull BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    private YamlConfiguration locale;
    private final String root = "messages";

    public MessagesHandler(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String name() {
        return "Messages";
    }

    @Override
    protected void onLoad() {
        LOCALE_FILE = new File(plugin.getDataFolder()+File.separator+"messages.yml");
        loadMessagesHandler();
        this.adventure = BukkitAudiences.create(PetPlugin.getInstance());
    }

    @Override
    protected void onUnload() {

    }

    private void loadMessagesHandler() {
        String localeName = plugin.getConfig().getString("locale", null);
        if(localeName != null) {
            LOCALE_FILE = new File(plugin.getDataFolder()+File.separator+"locales/"+localeName+".yml");
        }
        this.locale = YamlConfiguration.loadConfiguration(LOCALE_FILE);
    }

    public String getRawString(String subpath) {
        return this.locale.getString(absolutePath(subpath));
    }

    public Component getComponent(String subpath) {
        return ComponentsHelper.simpleParse(getRawString(subpath));
    }

    public Component getParsedComponent(Player player, String subpath) {
        String text = getRawString(subpath);
        String parsed = parseDefaultPlaceholders(player, text);
        if(PetPlugin.getInstance().getModulesHandler().hasModule("PlaceholderAPI")) {
            PapiModule papi = (PapiModule) PetPlugin.getInstance().getModulesHandler().moduleByName("PlaceholderAPI");
            parsed = papi.parsePlaceholders(player, parsed);
        }
        return MiniMessage.miniMessage().deserialize(parsed);
    }

    public void sendMessage(Player player, String subpath) {
        sendMessage(player, getParsedComponent(player, subpath));
    }

    public void sendMessage(Player player, Component message) {
        adventure().player(player).sendMessage(message);
    }

    private String absolutePath(@NotNull String subpath) {
        return this.root+"."+subpath;
    }

    private String parseDefaultPlaceholders(Player player, String text) {
        Dictionary<String, String> placeholders = buildDictionary(player);
        for (Iterator<String> it = placeholders.keys().asIterator(); it.hasNext(); ) {
            String key = it.next();
            text = text.replace(key, placeholders.get(key));
        }
        return text;
    }

    private Dictionary<String, String> buildDictionary(Player player) {
        Dictionary<String, String> placeholders = new Hashtable<>();
        UserPet userPet = PetPlugin.getApi().userPet(player);
        if(userPet != null) {
            placeholders.put("{type}", userPet.getPetType().getName());
            placeholders.put("{pet}", userPet.getPetType().getName());
            placeholders.put("{level}", userPet.getLevel()+"");
        }
        placeholders.put("{player}", player.getName());
        return placeholders;
    }

}
