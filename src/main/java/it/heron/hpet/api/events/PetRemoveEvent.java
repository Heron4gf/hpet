package it.heron.hpet.api.events;

import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PetRemoveEvent extends PetEvent {
    public PetRemoveEvent(@NotNull Player who, UserPet pet) {
        super(who, pet);
    }
}
