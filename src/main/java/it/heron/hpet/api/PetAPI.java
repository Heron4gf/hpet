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

import it.heron.hpet.database.tables.LastPet;
import it.heron.hpet.modules.pets.PetsHandler;
import it.heron.hpet.modules.pets.pettypes.PetType;
import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;
import lombok.NonNull;
import org.bukkit.entity.Entity;
import it.heron.hpet.main.PetPlugin;
import org.bukkit.entity.Player;

import java.util.*;

public class PetAPI {

    /**
     * Returns a collection of all currently spawned user pets in the system.
     *
     * @return a collection of active {@link UserPet} instances
     */
    public Collection<UserPet> spawnedPets() {
        PetsHandler petsHandler = (PetsHandler) PetPlugin.getInstance().getModulesHandler().moduleByName("PetsHandler");
        return petsHandler.spawnedPets();
    }

    /**
     * Determines whether the specified entity owns at least one pet.
     *
     * @param owner the entity whose pet ownership is being checked
     * @return {@code true} if the entity owns at least one pet; {@code false} otherwise
     */
    public boolean hasUserPet(@NonNull Entity owner) {
        return userPet(owner) != null;
    }

    /**
     * Returns all pets owned by the specified entity.
     *
     * @param owner the entity whose pets are to be retrieved
     * @return a set of UserPet instances owned by the entity
     */
    public Set<UserPet> userPets(@NonNull Entity owner) {
        return userPets(owner.getUniqueId());
    }

    /**
     * Returns all pets associated with the specified owner's UUID.
     *
     * @param owner the UUID of the pet owner
     * @return a set of UserPet instances owned by the given UUID
     */
    public Set<UserPet> userPets(@NonNull UUID owner) {
        PetsHandler petsHandler = (PetsHandler) PetPlugin.getInstance().getModulesHandler().moduleByName("PetsHandler");
        return petsHandler.userPets(owner);
    }

    /**
     * Returns the first pet owned by the specified entity, or {@code null} if the entity has no pets.
     *
     * @param owner the entity whose first pet is to be retrieved
     * @return the first {@link UserPet} owned by the entity, or {@code null} if none exist
     */
    public UserPet userPet(@NonNull Entity owner) {
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
     * Selects and spawns a pet for the specified entity using the provided pet type name.
     *
     * @param owner the entity for whom the pet will be spawned
     * @param petType the name of the pet type to spawn
     * @return the spawned {@link UserPet}, or {@code null} if the pet type does not exist or selection fails
     */
    public UserPet selectPet(@NonNull Entity owner, @NonNull String petType) {
        return selectPet(owner, petType(petType));
    }

    /**
     * Selects and spawns a pet of the specified type for the given entity.
     *
     * @param owner the entity that will own the pet
     * @param petType the type of pet to spawn
     * @return the created UserPet instance, or null if the pet could not be selected or spawned
     */
    public UserPet selectPet(@NonNull Entity owner, @NonNull PetType petType) {
        PetsHandler petsHandler = (PetsHandler) PetPlugin.getInstance().getModulesHandler().moduleByName("PetsHandler");
        return petsHandler.selectPet(owner, petType);
    }

    /**
     * Removes the specified pet from the game and disassociates it from its owner.
     *
     * @param userPet the pet instance to remove
     */
    public void removePet(@NonNull UserPet userPet) {
        PetsHandler petsHandler = (PetsHandler) PetPlugin.getInstance().getModulesHandler().moduleByName("PetsHandler");
        petsHandler.removePet(userPet);
    }

    /**
     * Loads and spawns the last pet type used by the specified player from the database.
     *
     * @param owner the player whose last used pet should be spawned
     */
    public void spawnDatabasePet(Player owner) {
        LastPet lastPet = LastPet.load(owner.getUniqueId());
        selectPet(owner, lastPet.getPetType());
    }

}