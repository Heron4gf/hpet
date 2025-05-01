package it.heron.hpet.api.events;

import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public abstract class PetEvent extends PlayerEvent {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Getter
    private UserPet pet;

    /**
     * Constructs a new PetEvent for the specified player and associated user pet.
     *
     * @param who the player involved in the event
     * @param pet the user pet related to this event
     */
    public PetEvent(@NotNull Player who, UserPet pet) {
        super(who);
        this.pet = pet;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
