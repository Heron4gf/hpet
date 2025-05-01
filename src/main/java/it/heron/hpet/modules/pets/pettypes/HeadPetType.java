package it.heron.hpet.modules.pets.pettypes;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.utils.heads.CustomHead;
import it.heron.hpet.utils.heads.HDBHead;
import it.heron.hpet.utils.heads.PlayerHead;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class HeadPetType extends StackPetType {
    /**
     * Constructs a HeadPetType with the specified YAML configuration and key.
     *
     * @param configuration the YAML configuration for this pet type
     * @param key the unique identifier for this pet type
     */
    public HeadPetType(YamlConfiguration configuration, String key) {
        super(configuration, key);
    }

    /**
     * Creates an ItemStack representing a pet head skin based on the provided skin name.
     *
     * Depending on the format and content of the skinName, this method returns a custom head, a HeadDatabase head, or a player head.
     *
     * @param skinName the identifier for the desired head skin
     * @return an ItemStack representing the specified head skin
     */
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
