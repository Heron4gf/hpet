package it.heron.hpet.combat;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.pettypes.PetType;
import nl.marido.deluxecombat.events.CombatStateChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Deluxe implements Listener {

    @EventHandler
    void onCombat(CombatStateChangeEvent event) {
        switch(event.getState()) {
            case TAGGED:
                removePet(event.getPlayer());
                break;
            case UNTAGGED:
                PetPlugin.getApi().selectPet(event.getPlayer(), disabledPet.get(event.getPlayer().getUniqueId()));
                disabledPet.remove(event.getPlayer().getUniqueId());
                break;
        }
    }

    private Map<UUID, PetType> disabledPet = new HashMap<>();
    private void removePet(Player p) {
        if(PetPlugin.getApi().hasUserPet(p)) {
            disabledPet.put(p.getUniqueId(), PetPlugin.getApi().getUserPet(p).getType());
            PetPlugin.getApi().getUserPet(p).remove();
        }
    }

}