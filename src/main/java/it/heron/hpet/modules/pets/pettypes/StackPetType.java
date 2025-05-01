package it.heron.hpet.modules.pets.pettypes;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.utils.heads.CustomHead;
import it.heron.hpet.utils.heads.HDBHead;
import it.heron.hpet.utils.heads.PlayerHead;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public abstract class StackPetType extends AbstractPetType {

    @Getter
    protected ItemStack[] skins;

    /**
     * Initializes the StackPetType by loading skin names from the configuration and creating corresponding ItemStack skins.
     *
     * @param configuration the YAML configuration containing skin definitions
     * @param key the configuration key for this pet type
     */
    public StackPetType(YamlConfiguration configuration, String key) {
        super(configuration, key);
        List<String> skins = configuration.getStringList(absolutePath("skins"));
        this.skins = new ItemStack[skins.size()];
        for (int i = 0; i < this.skins.length; i++) {
            this.skins[i] = makeSkin(skins.get(i));
        }

    }

    /**
     * Returns {@code null} to indicate that this pet type does not provide a GUI icon for the specified viewer.
     *
     * @param viewer the player for whom the GUI icon would be generated
     * @return always {@code null}
     */
    @Override
    public ItemStack generateGuiIcon(Player viewer) {
        return null;
    }

    /**
     * Indicates that this pet type cannot be purchased by any player.
     *
     * @param player the player attempting to purchase the pet
     * @return always {@code false}
     */
    @Override
    public boolean canBuy(Player player) {
        return false;
    }

    /**
 * Creates an {@link ItemStack} representing a pet skin based on the provided skin name.
 *
 * @param skinName the name of the skin to create
 * @return an ItemStack corresponding to the specified skin
 */
protected abstract ItemStack makeSkin(String skinName);
}
