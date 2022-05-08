package it.heron.hpet.vanish;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Vanish {
    public boolean isVanished(Player p) { return p.spigot().getHiddenPlayers().contains(p); }
    public boolean isInvisible(Player p) { return p.getGameMode() == GameMode.SPECTATOR; }
}
