package it.heron.hpet.combat;

import it.heron.hpet.Pet;
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
                Pet.getApi().selectPet(event.getPlayer(), disabledPet.get(event.getPlayer().getUniqueId()));
                disabledPet.remove(event.getPlayer().getUniqueId());
                break;
        }
    }

    private Map<UUID, PetType> disabledPet = new HashMap<>();
    private void removePet(Player p) {
        if(Pet.getApi().hasUserPet(p)) {
            disabledPet.put(p.getUniqueId(), Pet.getApi().getUserPet(p).getType());
            Pet.getApi().getUserPet(p).remove();
        }
    }

}