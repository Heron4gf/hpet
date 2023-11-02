package it.heron.hpet.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HPETReloadPluginEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public HPETReloadPluginEvent() {

    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
