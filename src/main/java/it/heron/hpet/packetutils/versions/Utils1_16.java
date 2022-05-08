package it.heron.hpet.packetutils.versions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.inventory.ItemStack;
import it.heron.hpet.packetutils.PacketUtils;

import java.util.LinkedList;
import java.util.List;

public class Utils1_16 extends PacketUtils {

    @Override
    public int slotGlow() {return 0;}
    @Override
    public int slotSmall() {return 14;}
    @Override
    public WrappedDataWatcher getDataWatcher(PacketContainer entityMetadata) {return new WrappedDataWatcher((List)entityMetadata.getWatchableCollectionModifier().read(0));}
    @Override
    public Vector3F getPose() {return new Vector3F(-44.0F, 34.0F, 1.0F);}
    @Override
    public int slotHand() {return 18;}

    @Override
    public int slotSmallMob() {
        return 15;
    }


    @Override
    public PacketContainer equipItem(int entityID, EnumWrappers.ItemSlot itemSlot, ItemStack item) {

        PacketContainer entityEquipment = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);

        entityEquipment.getIntegers().write(0, entityID);

        List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment = new LinkedList<>();
        equipment.add(new Pair<>(itemSlot, item));
        entityEquipment.getSlotStackPairLists().write(0, equipment);

        return entityEquipment;

    }

    @Override
    public boolean isLegacy() {
        return false;
    }


    @Override
    public PacketContainer destroyEntity(int entityID) {

        PacketContainer entityDestroy = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);

            int[] entityIDs = new int[1];
            entityIDs[0] = entityID;
            entityDestroy.getIntegerArrays().write(0, entityIDs);

        return entityDestroy;

    }

}
