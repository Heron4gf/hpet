package it.heron.hpet.modules.pets.pettypes;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public class HeadPetType extends AbstractPetType {

    private ItemStack[] skins;

    public HeadPetType(YamlConfiguration configuration, String key) {
        super(configuration, key);
        List<String> skins = configuration.getStringList(absolutePath("skins"));
        try {
            this.skins = new ItemStack[skins.size()];
            for (int i = 0; i < this.skins.length; i++) {
                this.skins[i] = makeSkin(skins.get(i));
            }
        } catch (Exception ignored) {
            this.skins = new ItemStack[]{makeSkin("Steve")};
            Bukkit.getLogger().severe("Your skin formatting for the pet "+key+" is invalid!");
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

    private ItemStack makeSkin(String skinName) {
        return null;
    }
}
