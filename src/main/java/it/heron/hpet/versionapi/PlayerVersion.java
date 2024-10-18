package it.heron.hpet.versionapi;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.packetutils.PacketUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerVersion {

    @Getter
    private int serverProtocol = versionToProtocol();
    int versionToProtocol() {
        String version = Bukkit.getServer().getVersion();
        if(version.contains("1.17")) {
            return 756;
        }
        if(version.contains("1.16")) {
            return 754;
        }
        if(version.contains("1.15")) {
            return 578;
        }
        if(version.contains("1.8")) {
            return 47;
        }
        return 340;
    }

    public boolean isUsingServerVersion(Player p) {
        return true;
    }
    public int getPlayerVersion(Player p) {
        return getServerProtocol();
    }
    public PacketUtils getPlayerPackets(Player p) {
        return PetPlugin.getInstance().getPacketUtils();
    }
}
