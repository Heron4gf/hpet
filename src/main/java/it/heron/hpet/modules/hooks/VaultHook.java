package it.heron.hpet.modules.hooks;

import it.heron.hpet.modules.abstracts.PluginHook;
import org.bukkit.plugin.java.JavaPlugin;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;

public class VaultHook extends PluginHook {

    @Getter
    private Economy economy = null;

    /**
     * Constructs a VaultHook instance and initializes it with the given plugin.
     *
     * @param plugin the JavaPlugin instance to associate with this hook
     */
    public VaultHook(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Returns the name of the hook, identifying it as "Vault".
     *
     * @return the string "Vault"
     */
    @Override
    public String name() {
        return "Vault";
    }

    @Override
    protected void onLoad() {
        this.economy = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
    }

    @Override
    protected void onUnload() {
        this.economy = null;
    }
}
