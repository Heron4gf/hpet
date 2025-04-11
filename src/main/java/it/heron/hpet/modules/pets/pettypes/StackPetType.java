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

    public StackPetType(YamlConfiguration configuration, String key) {
        super(configuration, key);
        List<String> skins = configuration.getStringList(absolutePath("skins"));
        this.skins = new ItemStack[skins.size()];
        for (int i = 0; i < this.skins.length; i++) {
            this.skins[i] = makeSkin(skins.get(i));
        }

    }

    @Override
    public ItemStack generateGuiIcon(Player viewer) {
        return null;
    }

    @Override
    public boolean canBuy(Player player) {
        return false;
    }

    protected abstract ItemStack makeSkin(String skinName);
}
