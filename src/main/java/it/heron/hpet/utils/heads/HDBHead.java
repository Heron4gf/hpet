package it.heron.hpet.utils.heads;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.hooks.HeadDatabaseModule;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.inventory.ItemStack;

public class HDBHead extends HeadFromString {

    public HDBHead(String value) {
        super(value);
    }

    @Override
    public ItemStack generate() {
        HeadDatabaseModule module = (HeadDatabaseModule)PetPlugin.getInstance().getModulesHandler().moduleByName("HeadDatabase");
        HeadDatabaseAPI headAPI = module.getHeadAPI();
        return headAPI.getItemHead(value);
    }
}
