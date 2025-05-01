package it.heron.hpet.modules.hooks;

import it.heron.hpet.modules.abstracts.PluginHook;
import org.bukkit.plugin.java.JavaPlugin;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;

public class VaultHook extends PluginHook {

    @Getter
    private Economy economy = null;

    public VaultHook(JavaPlugin plugin) {
        super(plugin);
    }

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
