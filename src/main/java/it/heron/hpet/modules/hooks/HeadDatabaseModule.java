package it.heron.hpet.modules.hooks;

import it.heron.hpet.modules.abstracts.PluginHook;
import lombok.Getter;
import me.arcaniax.hdb.api.HeadDatabaseAPI;

public class HeadDatabaseModule extends PluginHook {

    @Getter
    private HeadDatabaseAPI headAPI;

    @Override
    public String name() {
        return "HeadDatabase";
    }

    @Override
    protected void onLoad() {
        this.headAPI = new HeadDatabaseAPI();
    }

    @Override
    protected void onUnload() {

    }
}
