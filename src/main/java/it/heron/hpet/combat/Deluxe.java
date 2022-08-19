package it.heron.hpet.combat;

import it.heron.hpet.Pet;
import nl.marido.deluxecombat.events.CombatlogEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Deluxe implements Listener {

    @EventHandler
    void onCombat(CombatlogEvent event) {
        removePet(event.getCombatlogger());
        removePet(event.getLastAttacker());
    }
    private void removePet(Player p) {
        if(Pet.getApi().hasUserPet(p)) Pet.getApi().getUserPet(p).remove();
    }

}
