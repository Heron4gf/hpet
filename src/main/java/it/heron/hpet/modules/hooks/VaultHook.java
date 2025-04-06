package it.heron.hpet.modules.hooks;

import it.heron.hpet.modules.abstracts.PluginHook;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;

public class VaultHook extends PluginHook {

    @Getter
    private Economy economy = null;

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
