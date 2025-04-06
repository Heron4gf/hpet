package it.heron.hpet.modules.pets.userpets.fakeentities.armorstandmetadatahandlers;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.util.Vector3f;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractMetadataHandler implements ArmorStandMetadataHandler {

    protected abstract EntityData invisible();
    protected abstract EntityData name(Component name);
    protected abstract EntityData small();
    protected abstract EntityData glow();
    protected abstract EntityData marker();
    protected abstract EntityData showArms();
    protected abstract EntityData armData();
    protected abstract Vector3f armPose();

    @Override
    public List<EntityData> metadata(Component name, boolean small, boolean glow) {
        List<EntityData> entityDatas = Arrays.asList(invisible(), name(name), marker(), showArms(), armData());
        if(small) entityDatas.add(small());
        if(glow) entityDatas.add(glow());
        return entityDatas;
    }
}
