package it.heron.hpet.vanish;

import com.Zrips.CMI.CMI;
import org.bukkit.entity.Player;

public class CMIVanish extends Vanish {
    @Override
    public boolean isVanished(Player p) {
        try {
            return CMI.getInstance().getPlayerManager().getUser(p).isVanished();
        } catch(Exception ignored) {
            return false;
        }
    }
}
