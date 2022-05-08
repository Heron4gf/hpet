package it.heron.hpet.packetutils.versions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import java.util.List;
import java.util.Optional;

public class Utils1_17 extends Utils1_16 {

    @Override
    public PacketContainer setCustomName(int entityID, String name) {
        if(name == null) return null;

        PacketContainer entityMetadata = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);

        entityMetadata.getIntegers().write(0, entityID);

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();

        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional
                .of(WrappedChatComponent
                        .fromChatMessage(name)[0].getHandle()));

        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);

        entityMetadata.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());

        return entityMetadata;

    }

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
    public int slotSmall() {return 15;}

    @Override
    public int slotHand() {return 19;}

    @Override
    public WrappedDataWatcher getDataWatcher(PacketContainer entityMetadata) {return new WrappedDataWatcher();}

    @Override
    public int slotSmallMob() {return 16;}


}
