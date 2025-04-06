package it.heron.hpet.modules.pets.userpets.fakeentities.armorstandmetadatahandlers.versions;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Vector3f;
import it.heron.hpet.modules.pets.userpets.fakeentities.armorstandmetadatahandlers.AbstractMetadataHandler;
import net.kyori.adventure.text.Component;

public class Metadata1_21 extends AbstractMetadataHandler {
    @Override
    protected EntityData invisible() {
        return new EntityData(0, EntityDataTypes.BYTE, 0x20);
    }

    @Override
    protected EntityData name(Component name) {
        return new EntityData(2, EntityDataTypes.OPTIONAL_ADV_COMPONENT, name);
    }

    @Override
    protected EntityData small() {
        return new EntityData(15, EntityDataTypes.BYTE, 0x01);
    }

    @Override
    protected EntityData glow() {
        return new EntityData(0, EntityDataTypes.BYTE, 0x40);
    }

    @Override
    protected EntityData marker() {
        return new EntityData(15, EntityDataTypes.BYTE, 0x10);
    }

    @Override
    protected EntityData showArms() {
        return new EntityData(15, EntityDataTypes.BYTE, 0x04);
    }

    @Override
    protected EntityData armData() {
        return new EntityData(18, EntityDataTypes.ROTATION, armPose());
    }

    @Override
    protected Vector3f armPose() {
        return new Vector3f(-44.0F, 34.0F, 1.0F);
    }
}
