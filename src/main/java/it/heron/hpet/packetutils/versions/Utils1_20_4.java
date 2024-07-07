package it.heron.hpet.packetutils.versions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import it.heron.hpet.Pet;

public class Utils1_20_4 extends Utils1_19_3 {
    public void initDestroyListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Pet.getInstance(), new PacketType[]{PacketType.Play.Server.ENTITY_DESTROY}) {
            public void onPacketSending(PacketEvent event) {
                try {
                    int id = event.getPacket().getIntLists().readSafely(0).get(0);
                    if(destroyQueue.contains(id)) {
                        event.setCancelled(true);
                    }
                } catch (Exception ignored) {}
            }
        });
    }
}
