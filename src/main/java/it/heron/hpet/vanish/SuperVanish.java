package it.heron.hpet.vanish;

import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.entity.Player;

public class SuperVanish extends Vanish {
    @Override
    public boolean isVanished(Player p) {
        return VanishAPI.isInvisible(p);
    }
}
