package it.heron.hpet.versionapi;

import com.viaversion.viaversion.api.Via;
import it.heron.hpet.packetutils.versions.PacketUtils;
import it.heron.hpet.packetutils.versions.*;
import org.bukkit.entity.Player;

public class ViaVer extends PlayerVersion {
    public boolean isUsingServerVersion(Player p) {
        return getServerProtocol()==getPlayerVersion(p);
    }
    public int getPlayerVersion(Player p) {
        return Via.getAPI().getPlayerVersion(p.getUniqueId());
    }
    public PacketUtils getPlayerPackets(Player p) {
        int protocol = getPlayerVersion(p);
        if(protocol > 754) return new Utils1_17();
        if(protocol > 578) return new Utils1_16();
        //if(protocol > 340) return new Utils1_15();
        if(protocol > 47) return new Utils1_12();
        return new Utils1_8();
    }
}
