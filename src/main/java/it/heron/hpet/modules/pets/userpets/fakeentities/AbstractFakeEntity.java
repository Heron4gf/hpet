package it.heron.hpet.modules.pets.userpets.fakeentities;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractFakeEntity implements FakeEntity {

    @Getter
    protected int id = SpigotReflectionUtil.generateEntityId();

    @Getter
    protected UUID uuid = UUID.randomUUID();

    protected World spawnedWorld = null;

    @Override
    public boolean isSpawned() {
        return this.spawnedWorld != null;
    }

    @Override
    public void spawn(Location location) {
        if(isSpawned()) return;
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(
                id,
                uuid,
                entityType(),
                convertedLocation(location),
                location.getYaw(), // Head yaw
                0, // No additional data
                null // We won't specify any initial velocity
        );
        sendPacketInWorld(location.getWorld(), packet);
        this.spawnedWorld = location.getWorld();
        onSpawn();
    }

    @Override
    public void despawn() {
        if(!isSpawned()) return;
        WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(this.id);
        sendPacketInWorld(this.spawnedWorld, packet);
        this.spawnedWorld = null;
        onDespawn();
    }

    @Override
    public void teleport(Location location, boolean onGround) {
        if(!Objects.equals(location.getWorld(), spawnedWorld)) {
            despawn();
            spawn(location);
            return;
        }
        WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport(this.id, convertedLocation(location), onGround);
        sendPacket(packet);
    }

    protected void sendPacket(Object packet) {
        sendPacketInWorld(this.spawnedWorld, packet);
    }

    private void sendPacketInWorld(World world, Object packet) {
        for(Player player : world.getPlayers()) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
        }
    }

    private com.github.retrooper.packetevents.protocol.world.Location convertedLocation(Location location) {
        return new com.github.retrooper.packetevents.protocol.world.Location(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    protected abstract void onSpawn();
    protected abstract void onDespawn();
}
