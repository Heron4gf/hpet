package it.heron.hpet.modules.pets.userpets.fakeentities.armorstandmetadatahandlers;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import net.kyori.adventure.text.Component;

import java.util.List;

public interface ArmorStandMetadataHandler {

    List<EntityData> metadata(Component name, boolean small, boolean glow);

}
