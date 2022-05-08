package it.heron.hpet.packetutils.versions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.inventory.ItemStack;

public class Utils1_15 extends Utils1_16 {


    @Override
    public PacketContainer equipItem(int entityID, EnumWrappers.ItemSlot itemSlot, ItemStack item) {

        PacketContainer entityEquipment = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);

        entityEquipment.getIntegers().write(0, entityID);

            entityEquipment.getIntegers().write(0, entityID);
            entityEquipment.getItemSlots().write(0, itemSlot);
            entityEquipment.getItemModifier().write(0, item);

        return entityEquipment;

    }
}
