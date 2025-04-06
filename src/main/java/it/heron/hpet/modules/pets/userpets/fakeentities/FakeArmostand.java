package it.heron.hpet.modules.pets.userpets.fakeentities;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import it.heron.hpet.main.PetPlugin;
import lombok.Getter;
import net.kyori.adventure.text.Component;


public class FakeArmostand extends AbstractFakeEntity {

    @Getter
    private Component name;
    @Getter
    private boolean glow;
    @Getter
    private boolean small;

    public void setName(Component name) {
        this.name = name;
        updateMetadata();
    }

    public void setGlow(boolean glow) {
        this.glow = glow;
        updateMetadata();
    }

    public void setSmall(boolean small) {
        this.small = small;
        updateMetadata();
    }

    public FakeArmostand(Component name, boolean glow, boolean small) {
        this.name = name;
        this.glow = glow;
        this.small = small;
    }

    @Override
    protected void onSpawn() {
        updateMetadata();
    }

    @Override
    protected void onDespawn() {

    }

    private void updateMetadata() {
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(
                this.id, PetPlugin.getInstance().getArmorStandMetadataHandler().metadata(name, small, glow)
        );
        sendPacket(packet);
    }

    @Override
    public int requiredVersionProtcol() {
        return 47;
    }

    @Override
    public EntityType entityType() {
        return EntityTypes.ARMOR_STAND;
    }
}
