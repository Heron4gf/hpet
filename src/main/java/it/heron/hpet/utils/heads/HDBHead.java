package it.heron.hpet.utils.heads;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.hooks.HeadDatabaseModule;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.inventory.ItemStack;

public class HDBHead extends HeadFromString {

    /**
     * Constructs an HDBHead with the specified head identifier.
     *
     * @param value the identifier used to retrieve the head from HeadDatabase
     */
    public HDBHead(String value) {
        super(value);
    }

    /**
     * Generates an ItemStack representing a custom head from the HeadDatabase plugin using the stored identifier.
     *
     * @return the ItemStack corresponding to the head associated with the stored value
     */
    @Override
    public ItemStack generate() {
        HeadDatabaseModule module = (HeadDatabaseModule)PetPlugin.getInstance().getModulesHandler().moduleByName("HeadDatabase");
        HeadDatabaseAPI headAPI = module.getHeadAPI();
        return headAPI.getItemHead(value);
    }
}
