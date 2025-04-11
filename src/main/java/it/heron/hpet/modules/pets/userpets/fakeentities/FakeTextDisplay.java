package it.heron.hpet.modules.pets.userpets.fakeentities;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class FakeTextDisplay extends AbstractFakeEntity implements CanHaveItemOnHead {

    @Getter
    private Component text;

    public FakeTextDisplay(Component text) {
        this.text = text;
    }

    @Override
    protected void onSpawn() {
        updateText();
    }

    @Override
    protected void onDespawn() {

    }

    @Override
    public int requiredVersionProtcol() {
        return 762;
    }

    @Override
    public EntityType entityType() {
        return EntityTypes.TEXT_DISPLAY;
    }

    public void setText(Component text) {
        this.text = text;
        updateText();
    }

    private void updateText() {
        List<EntityData> entityData = Arrays.asList(
                new EntityData(23, EntityDataTypes.ADV_COMPONENT, text)
        );
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(this.id, entityData);
        sendPacket(packet);
    }

    @Override
    public void setHeadItem(ItemStack itemStack) {

    }
}
