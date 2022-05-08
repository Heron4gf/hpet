package it.heron.hpet.vanish;

import com.earth2me.essentials.Essentials;
import org.bukkit.entity.Player;

public class EssentialsVanish extends Vanish {

    private Essentials ess = (Essentials) Essentials.getProvidingPlugin(Essentials.class);

    @Override
    public boolean isVanished(Player p) { return ess.getUser(p).isVanished(); }

}
