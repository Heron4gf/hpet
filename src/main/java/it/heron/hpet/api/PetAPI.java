/**
 * The {@code PetAPI} class provides a public interface for interacting with
 * the HPET plugin's pet system, including querying, selecting, and removing pets.
 *
 * <p>This class allows developers to retrieve pets owned by an entity or UUID,
 * access available pet types, and manage a user's current pet.
 *
 * <p><strong>Note:</strong> Usage is subject to the plugin's Terms of Service.
 * Redistribution or reverse engineering without permission is prohibited.
 */
package it.heron.hpet.api;

import it.heron.hpet.modules.pets.pettypes.PetType;
import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;
import org.bukkit.entity.Entity;
import it.heron.hpet.main.PetPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PetAPI {

    public Collection<UserPet> spawnedPets() {
        return PetPlugin.getInstance().getPetsHandler().
    }

    /**
     * Checks if a given entity has at least one pet.
     *
     * @param owner The entity to check.
     * @return {@code true} if the entity has a pet, {@code false} otherwise.
     */
    public boolean hasUserPet(@NotNull Entity owner) {
        return userPet(owner) != null;
    }

    /**
     * Retrieves all pets owned by the given entity.
     *
     * @param owner The entity whose pets are being retrieved.
     * @return A set of {@link UserPet} instances owned by the entity.
     */
    public Set<UserPet> userPets(@NotNull Entity owner) {
        return userPets(owner.getUniqueId());
    }

    /**
     * Retrieves all pets owned by the given UUID.
     *
     * @param owner The UUID of the pet owner.
     * @return A set of {@link UserPet} instances owned by the UUID.
     */
    public Set<UserPet> userPets(@NotNull UUID owner) {
        return PetPlugin.getInstance().getPetsHandler().userPets(owner);
    }

    /**
     * Retrieves the first pet owned by a given entity.
     *
     * @param owner The entity whose primary pet is being retrieved.
     * @return A {@link UserPet} instance or {@code null} if the entity has no pets.
     */
    public UserPet userPet(@NotNull Entity owner) {
        try {
            return userPets(owner).iterator().next();
        } catch (NoSuchElementException ignored) {
            return null;
        }
    }

    /**
     * Retrieves a pet type by its name.
     *
     * @param name The name of the pet type.
     * @return The corresponding {@link PetType}, or {@code null} if not found.
     */
    public PetType petType(String name) {
        return PetPlugin.getInstance().getPetTypesHandler().petType(name);
    }

    /**
     * Returns a collection of all enabled and loaded pet types.
     *
     * @return A collection of {@link PetType} instances.
     */
    public Collection<PetType> enabledPetTypes() {
        return PetPlugin.getInstance().getPetTypesHandler().loadedPetTypes();
    }

    /**
     * Selects and spawns a pet for the specified entity using the given pet type name.
     *
     * @param owner The entity who will own the pet.
     * @param petType The name of the pet type to select.
     * @return The newly selected {@link UserPet}, or {@code null} if selection failed.
     */
    public UserPet selectPet(@NotNull Entity owner, @NotNull String petType) {
        return selectPet(owner, petType(petType));
    }

    /**
     * Selects and spawns a pet for the specified entity using a {@link PetType}.
     *
     * @param owner The entity who will own the pet.
     * @param petType The pet type to assign.
     * @return The newly selected {@link UserPet}, or {@code null} if selection failed.
     */
    public UserPet selectPet(@NotNull Entity owner, @NotNull PetType petType) {
        return PetPlugin.getInstance().getPetsHandler().selectPet(owner, petType);
    }

    /**
     * Removes a pet from the game and its owner's list.
     *
     * @param userPet The {@link UserPet} to be removed.
     */
    public void removePet(UserPet userPet) {
        PetPlugin.getInstance().getPetsHandler().removePet(userPet);
    }

}