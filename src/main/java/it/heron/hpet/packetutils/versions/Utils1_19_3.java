package it.heron.hpet.packetutils.versions;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import it.heron.hpet.packetutils.PacketUtils;

import java.util.List;
import java.util.Objects;

public class Utils1_19_3 extends Utils1_17 {

    @Override
    public PacketContainer standardMetaData(PacketContainer entityMetadata, PacketUtils protocol) {
        WrappedDataWatcher dataWatcher = getDataWatcher(entityMetadata);


        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(slotHand(), WrappedDataWatcher.Registry.getVectorSerializer()), protocol.getPose());

        final List<WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
        dataWatcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> {
            final WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = entry.getWatcherObject();
            wrappedDataValueList.add(new WrappedDataValue(dataWatcherObject.getIndex(), dataWatcherObject.getSerializer(), entry.getRawValue()));
        });

        entityMetadata.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        return entityMetadata;
    }
}
