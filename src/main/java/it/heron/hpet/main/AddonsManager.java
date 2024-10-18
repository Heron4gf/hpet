package it.heron.hpet.main;

import it.heron.hpet.updater.HDEVCommandHandler;
import lombok.Getter;

public class AddonsManager {

    public static AddonsManager INSTANCE;

    @Getter
    private PetPlugin petPlugin = PetPlugin.getInstance();

    /* the instance bukkit has of
    our plugin*/
    public AddonsManager() {
        /* "petPlugin" instance exists
         before "PetPluginInstance" instance, */
        new HDEVCommandHandler(petPlugin);
        INSTANCE = this;
    }

    public void reload() {

    }
}
