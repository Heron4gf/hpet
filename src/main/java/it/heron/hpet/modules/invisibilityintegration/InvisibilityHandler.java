package it.heron.hpet.modules.invisibilityintegration;

import it.heron.hpet.modules.abstracts.PluginHook;
import it.heron.hpet.modules.invisibilityintegration.hooks.CMIVanish;
import it.heron.hpet.modules.invisibilityintegration.hooks.EssentialsVanish;
import it.heron.hpet.modules.invisibilityintegration.hooks.SuperVanish;
import it.heron.hpet.modules.invisibilityintegration.vanilla.BukkitInvisibility;
import it.heron.hpet.modules.invisibilityintegration.vanilla.PotionInvisibility;
import it.heron.hpet.modules.invisibilityintegration.vanilla.SpectatorInvisibility;
import it.heron.hpet.modules.invisibilityintegration.vanilla.SpigotVanish;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;

import java.util.*;

public class InvisibilityHandler extends PluginHook {

    private Set<InvisibilityIntegration> invisibilityIntegrations = new HashSet<>();

    public InvisibilityHandler() {
        super();
        InvisibilityIntegration[] default_integrations = {
                new PotionInvisibility(),
                new SpectatorInvisibility(),
                new SpigotVanish(),
                new BukkitInvisibility()
        };
        this.invisibilityIntegrations.addAll(Arrays.asList(default_integrations));
        scanAndLoadSupportedPlugins();
    }

    @Override
    public String name() {
        return "Vanish";
    }

    @Override
    protected void onLoad() {
        scanAndLoadSupportedPlugins();
    }

    @Override
    protected void onUnload() {
        this.invisibilityIntegrations.clear();
    }

    private void scanAndLoadSupportedPlugins() {
        Dictionary<String, InvisibilityIntegration> dictionary = new Hashtable<>();
        dictionary.put("CMI", new CMIVanish());
        dictionary.put("Essentials", new EssentialsVanish());
        dictionary.put("SuperVanish", new SuperVanish());
        dictionary.put("PremiumVanish", new SuperVanish()); // set guarantees we cannot have duplicates
        PluginManager pluginManager = Bukkit.getPluginManager();
        for (Iterator<String> it = dictionary.keys().asIterator(); it.hasNext(); ) {
            String pluginName = it.next();
            if(super.canHook(pluginName)) {
                this.invisibilityIntegrations.add(dictionary.get(pluginName));
                Bukkit.getLogger().info("Integrating to "+pluginName+" to handle pet vanish");
            }
        }
    }

    public boolean isInvisible(Entity entity) {
        for(InvisibilityIntegration invisibilityIntegration : invisibilityIntegrations) {
            if(invisibilityIntegration.isInvisible(entity)) return true;
        }
        return false;
    }

    @Override
    public boolean canHook() {
        return true;
    }

}
