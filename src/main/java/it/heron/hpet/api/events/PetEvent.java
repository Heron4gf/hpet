package it.heron.hpet.api.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import it.heron.hpet.userpets.UserPet;

public abstract class PetEvent extends PlayerEvent {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Getter
    private UserPet pet;

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
