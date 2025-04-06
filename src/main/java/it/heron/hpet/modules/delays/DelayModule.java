package it.heron.hpet.modules.delays;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.abstracts.ListenerModule;
import it.heron.hpet.modules.exceptions.RefusedLoadException;
import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class DelayModule extends ListenerModule {

    @Getter
    private long delay;

    @Override
    protected void onLoad() {
        this.delay = plugin.getConfig().getLong("fix.delay."+name(), -1);
        if(delay == -1) throw new RefusedLoadException();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    protected void respawnFor(Player player) {
        for(UserPet userPet : PetPlugin.getApi().userPets(player)) {
            respawn(userPet);
        }
    }

    private void respawn(UserPet userPet) {
        userPet.setVisible(false);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            userPet.setVisible(true);
        }, delay);
    }

}
