package it.heron.hpet.packetutils.versions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import it.heron.hpet.Pet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.List;

public class Utils1_17 extends Utils1_16 {

    @Override
    public PacketContainer destroyEntity(int entityID) {

        PacketContainer entityDestroy = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);

            try {
                List<Integer> entityIDs = entityDestroy.getIntLists().readSafely(0);
                entityIDs.add(entityID);
                entityDestroy.getIntLists().writeSafely(0, entityIDs);
            }catch (Exception ex){
                entityDestroy.getIntegers().write(0, entityID);
            }

        return entityDestroy;

    }

    @Override
    public int slotHand() {return 19;}

    public void initDestroyListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Pet.getInstance(), new PacketType[]{PacketType.Play.Server.ENTITY_DESTROY}) {
            public void onPacketSending(PacketEvent event) {
                int id = event.getPacket().getIntLists().readSafely(0).get(0);
                if(destroyQueue.contains(id)) {
                    event.setCancelled(true);
                }
            }
        });
    }


    @Override
    public WrappedDataWatcher getDataWatcher(PacketContainer entityMetadata) {return new WrappedDataWatcher();}



}
