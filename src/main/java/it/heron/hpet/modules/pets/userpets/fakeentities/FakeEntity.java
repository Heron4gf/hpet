package it.heron.hpet.modules.pets.userpets.fakeentities;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import org.bukkit.Location;

import java.util.UUID;

public interface FakeEntity {

    int requiredVersionProtcol();
    EntityType entityType();

    int getId();
    UUID getUuid();

    void spawn(Location location);
    void despawn();

    void teleport(Location location, boolean onGround);

    boolean isSpawned();

}
