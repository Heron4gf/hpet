package it.heron.hpet.api.events;

import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PetRemoveEvent extends PetEvent {
    /**
     * Constructs a PetRemoveEvent for when a user removes a pet.
     *
     * @param who the player who removed the pet
     * @param pet the pet that was removed
     */
    public PetRemoveEvent(@NotNull Player who, UserPet pet) {
        super(who, pet);
    }
}
