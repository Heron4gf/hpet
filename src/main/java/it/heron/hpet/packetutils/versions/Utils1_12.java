package it.heron.hpet.packetutils.versions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.Vector3F;
import org.bukkit.Location;

public class Utils1_12 extends Utils1_15 {

    @Override
    public boolean isLegacy() {
        return true;
    }


    @Override
    public PacketContainer teleportEntity(int entityID, Location location, boolean precise) {
        /*Location location = loc;
        location.setYaw(location.getYaw()+180);*/

        PacketContainer teleportPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT);

        teleportPacket.getIntegers().write(0, entityID);

        teleportPacket.getDoubles().write(0, location.getX());
        teleportPacket.getDoubles().write(1, location.getY());
        teleportPacket.getDoubles().write(2, location.getZ());
        if(precise) {
            teleportPacket.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
            //teleportPacket.getBytes().write(1, (byte) (location.getPitch() * 256.0F / 360.0F));
        }

        return teleportPacket;
    }

    @Override
    public int slotHand() {return 15;}

    @Override
    public Vector3F getPose() {return new Vector3F(-44.0F, 34.0F, 1.0F);}


}
