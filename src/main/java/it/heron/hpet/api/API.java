package it.heron.hpet.api;

import it.heron.hpet.levels.LType;
import it.heron.hpet.levels.LevelEvents;
import it.heron.hpet.messages.Messages;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import it.heron.hpet.Pet;
import it.heron.hpet.userpets.UserPet;
import it.heron.hpet.groups.HSlot;

import java.util.Collection;
import java.util.List;

public class API {

    public boolean hasUserPet(Player p) {
        return getUserPet(p) != null;
    }
    public UserPet getUserPet(Player p) {
        return Pet.getInstance().getPacketUtils().getPets().get(p.getUniqueId());
    }
    public List<HSlot> getEnabledPetTypes() {
        return Pet.getInstance().getPetTypes();
    }
    public Collection<UserPet> getEnabledPets() {return Pet.getInstance().getPacketUtils().getPets().values();}
    public int getPetLevel(Player p, String type) { return Pet.getInstance().getDatabase().getPetLevel(p.getUniqueId(), type); }
    public void setPetLevel(Player p, String type, int amount) { Pet.getInstance().getDatabase().setPetLevel(p.getUniqueId(), type, amount); }

    public void incrementLevel(Player p) {
        UserPet upet = Pet.getApi().getUserPet(p);
        if(upet.getType().getLtype() == LType.NONE) return;
        int l = upet.getLevel()+1;
        setPetLevel(p, upet.getType().getName(), l);
        getUserPet(p).updateLevel();
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        for(String s : Messages.getList("levelup")) {
            p.sendMessage(s.replace("[level]", l+"").replace("[leveltype]", Messages.getMessage("leveltype."+upet.getType().getLtype().name())+" §7"+ LevelEvents.currentStat(upet)+"/"+LevelEvents.getMaxStat(upet)));
        }
    }

}
