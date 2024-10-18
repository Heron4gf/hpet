/*
 * This file is part of HPET - Packet Based Pet Plugin
 *
 * TOS (Terms of Service)
 * You are not allowed to decompile, or redestribuite part of this code if not authorized by the original author.
 * You are not allowed to claim this resource as yours.
 */
package it.heron.hpet.api;

import it.heron.hpet.api.events.PetSelectEvent;
import it.heron.hpet.levels.LType;
import it.heron.hpet.levels.LevelEvents;
import it.heron.hpet.messages.Messages;
import it.heron.hpet.pettypes.CosmeticType;
import it.heron.hpet.pettypes.PetType;
import it.heron.hpet.userpets.*;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.groups.HSlot;

import java.util.*;

public class API {

    public boolean hasUserPet(Player p) {
        return getUserPets(p) != null && !getUserPets(p).isEmpty();
    }
    public List<UserPet> getUserPets(Player p) {
        return PetPlugin.getInstance().getPacketUtils().playerPets(p.getUniqueId());
    }

    public UserPet getUserPet(Player player) {
        try {
            return getUserPets(player).get(0);
        } catch (Exception ignored) {
            return null;
        }
    }
    public List<HSlot> getEnabledPetTypes() {
        return PetPlugin.getInstance().getPetTypes();
    }
    //public Collection<UserPet> getEnabledPets() {return Pet.getInstance().getPacketUtils().getPets().values();}

    public UserPet selectPet(Player player, String petType) {
        return selectPet(player, PetPlugin.getPetTypeByName(petType));
    }
    public UserPet selectPet(Player player, PetType petType) {
        if(PetPlugin.getApi().hasUserPet(player)) {
            for(UserPet userPet : getUserPets(player)) {
                if(userPet.getType().getGroup().equals(petType.getGroup())) {
                    userPet.remove();
                }
            }
        }

        UserPet pet;
        if(petType.isMob()) {
            pet = new MobUserPet(player, petType, null);
        } else if(petType.isMythicMob()) {
            pet = new MythicUserPet(player, petType, null);
        } else if(petType instanceof CosmeticType && ((CosmeticType)petType).isWearable()) {
            pet = new PassengerUserPet(player.getUniqueId(), petType, null);
        } else {
            pet = new UserPet(player.getUniqueId(), petType, null);
        }

        Bukkit.getPluginManager().callEvent(new PetSelectEvent(player, pet));
        player.sendMessage(Messages.getMessage("pet.spawned").replace("[type]", petType.getName()));
        return pet;
    }

}
