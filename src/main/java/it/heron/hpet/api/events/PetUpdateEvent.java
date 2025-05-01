package it.heron.hpet.api.events;

import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PetUpdateEvent extends PetEvent {
    /**
     * Creates a new event representing an update to a user's pet.
     *
     * @param who the player associated with the pet update
     * @param pet the pet being updated
     */
    public PetUpdateEvent(@NotNull Player who, UserPet pet) {
        super(who, pet);
    }
}
