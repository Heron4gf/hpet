package it.heron.hpet.api.events;

import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PetSelectEvent extends PetEvent {
    /**
     * Constructs a new event representing a player selecting a pet.
     *
     * @param who the player who selected the pet
     * @param pet the pet that was selected
     */
    public PetSelectEvent(@NotNull Player who, UserPet pet) {
        super(who, pet);
    }
}
