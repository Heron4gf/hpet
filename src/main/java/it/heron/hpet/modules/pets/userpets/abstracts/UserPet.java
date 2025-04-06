package it.heron.hpet.modules.pets.userpets.abstracts;

import it.heron.hpet.modules.pets.pettypes.PetType;
import it.heron.hpet.modules.pets.userpets.animations.abstracts.IAnimation;
import it.heron.hpet.modules.pets.userpets.nametags.INametag;
import org.bukkit.Location;
// No need to import Entity here, we use UUID for the owner
import java.util.UUID;

/**
 * Represents a pet owned by a user (player or entity).
 * Defines the core properties and actions associated with a user's pet.
 */
public interface UserPet {

    /**
     * Gets the current location of the pet.
     * @return The pet's location.
     */
    Location getLocation();

    /**
     * Gets the type of this pet (e.g., appearance, base abilities).
     * @return The PetType.
     */
    PetType getPetType();

    /**
     * Gets the UUID of the owner entity.
     * @return The owner's UUID.
     */
    UUID getOwner();

    IAnimation getAnimation();

    INametag getNametag();

    void rename(String name);

    /**
     * Gets the current level of the pet.
     * @return The pet's level.
     */
    int getLevel();

    /**
     * Sets the level of the pet.
     * @param level The new level.
     */
    void setLevel(int level);

    /**
     * Checks if the pet is configured to be visible.
     * This is independent of the owner's vanish status or other temporary invisibility effects.
     * @return true if the pet is set to be generally visible, false otherwise.
     */
    boolean isVisible();

    boolean isVanished();

    /**
     * Sets whether the pet should be generally visible.
     * Note: Actual visibility also depends on the `vanished` state, typically linked to the owner.
     * @param visible true to make the pet potentially visible, false to always hide it.
     */
    void setVisible(boolean visible);

    /**
     * Gets the unique entity ID assigned to this pet by the server/packet system when spawned.
     * Returns -1 if the pet is not currently spawned or has no ID assigned.
     * @return The entity ID, or -1.
     */
    int getId();

    /**
     * Instantly moves the pet to a specific location.
     * Implementations should handle updating the pet's visual representation for nearby players.
     * @param location The target location.
     */
    void teleport(Location location);

    /**
     * Checks if the pet is currently spawned in the world and has an active entity ID.
     * @return true if the pet is spawned, false otherwise.
     */
    boolean isSpawned();

    /**
     * Spawns the pet into the world, making it potentially visible to players.
     * This usually involves assigning an entity ID and sending necessary spawn packets.
     */
    void spawn();

    /**
     * Despawns the pet from the world, removing its visual representation.
     * This usually involves sending destroy entity packets and invalidating the entity ID.
     */
    void despawn();

    /**
     * Executes a single logic tick for the pet.
     * This method is typically called periodically (e.g., every server tick) to handle
     * movement, AI, ability updates, visibility checks (syncing `vanished` with owner), etc.
     */
    void tick();

}