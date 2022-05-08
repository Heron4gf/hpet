package it.heron.hpet.api.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import it.heron.hpet.userpets.UserPet;

public class PetRemoveEvent extends PetEvent {
    public PetRemoveEvent(@NotNull Player who, UserPet pet) {
        super(who, pet);
    }
}
