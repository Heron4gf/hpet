package it.heron.hpet.modules.pets.pettypes;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.utils.heads.CustomHead;
import it.heron.hpet.utils.heads.HDBHead;
import it.heron.hpet.utils.heads.PlayerHead;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class HeadPetType extends StackPetType {
    public HeadPetType(YamlConfiguration configuration, String key) {
        super(configuration, key);
    }

    @Override
    protected ItemStack makeSkin(String skinName) {
        if(skinName.length() > 64) {
            return new CustomHead(skinName).get();
        } else if(skinName.startsWith("HDB") && PetPlugin.getInstance().getModulesHandler().hasModule("HeadDatabase")) {
            return new HDBHead(skinName.replace("HDB:", "")).get();
        } else {
            return new PlayerHead(skinName).get();
        }
    }
}
