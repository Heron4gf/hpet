package it.heron.hpet.api.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import it.heron.hpet.modules.pets.userpets.old.HeadUserPet;

public class PetUpdateEvent extends PetEvent {
    public PetUpdateEvent(@NotNull Player who, HeadUserPet pet) {
        super(who, pet);
    }
}
