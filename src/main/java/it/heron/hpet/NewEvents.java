/*
 * This file is part of HPET - Packet Based Pet Plugin
 *
 * TOS (Terms of Service)
 * You are not allowed to decompile, or redestribuite part of this code if not authorized by the original author.
 * You are not allowed to claim this resource as yours.
 */
package it.heron.hpet;

import it.heron.hpet.groups.HSlot;
import it.heron.hpet.pettypes.PetType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewEvents implements Listener {
    @EventHandler
    void onCommandTab(TabCompleteEvent event) {
        String buffer = event.getBuffer();
        if(!(buffer.startsWith("/hpet") || buffer.startsWith("/pet"))) {
            return;
        }
        String[] args = buffer.split(" ");
        if(args.length < 2) {
            event.setCompletions(Arrays.asList("select", "level", "setlevel", "addlevel", "reload", "update", "trail", "remove", "particle", "buy", "rename"));
            return;
        }
        if(args[1].equalsIgnoreCase("select") || args[1].equalsIgnoreCase("buy") || args[1].equalsIgnoreCase("addlevel") || args[1].equalsIgnoreCase("setlevel") || args[1].equalsIgnoreCase("removelevel")) {
            List<String> ptypes = new ArrayList<>();
            for(HSlot slot : Pet.getApi().getEnabledPetTypes()) {
                if(slot instanceof PetType) {
                    ptypes.add(slot.getName());
                }
            }
            event.setCompletions(ptypes);
        }
    }
}
