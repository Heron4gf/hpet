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
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class InvisibilityHandler extends PluginHook {

    private Set<InvisibilityIntegration> invisibilityIntegrations = new HashSet<>();

    public InvisibilityHandler(JavaPlugin plugin) {
        super(plugin);
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
        PluginManager pluginManager = Bukkit.getPluginManager();
        if(super.canHook("CMI")) {
            try {
                this.invisibilityIntegrations.add(new CMIVanish());
                Bukkit.getLogger().info("Integrating to CMI to handle pet vanish");
            } catch (NoClassDefFoundError e) {
                Bukkit.getLogger().warning("CMI classes not found - CMI integration disabled");
            }
        }
        if(super.canHook("Essentials")) {
            try {
                this.invisibilityIntegrations.add(new EssentialsVanish());
                Bukkit.getLogger().info("Integrating to Essentials to handle pet vanish");
            } catch (NoClassDefFoundError e) {
                Bukkit.getLogger().warning("Essentials classes not found - Essentials integration disabled");
            }
        }
        if(super.canHook("SuperVanish") || super.canHook("PremiumVanish")) {
            try {
                this.invisibilityIntegrations.add(new SuperVanish());
                Bukkit.getLogger().info("Integrating to SuperVanish/PremiumVanish to handle pet vanish");
            } catch (NoClassDefFoundError e) {
                Bukkit.getLogger().warning("SuperVanish classes not found - SuperVanish integration disabled");
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
