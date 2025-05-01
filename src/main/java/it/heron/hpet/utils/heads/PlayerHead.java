package it.heron.hpet.utils.heads;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerHead extends HeadFromString {

    public PlayerHead(String value) {
        super(value); // `value` is expected to be the player name
    }

    @Override
    public ItemStack generate() {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1); // Use PLAYER_HEAD for newer versions
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if (meta != null) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(value); // `value` is used as player name
            meta.setOwningPlayer(player);
            skull.setItemMeta(meta);
        }

        return skull;
    }
}
