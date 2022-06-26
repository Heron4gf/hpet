package it.heron.hpet.api;

import it.heron.hpet.levels.LType;
import it.heron.hpet.levels.LevelEvents;
import it.heron.hpet.messages.Messages;
import lombok.Data;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import it.heron.hpet.Pet;
import it.heron.hpet.userpets.UserPet;
import it.heron.hpet.groups.HSlot;

import java.util.*;

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


    private Set<LevelData> levelCache = new HashSet<>();
    public int getPetLevel(Player p, String type) {
        for(LevelData d : levelCache) {
            if(d.getUuid().equals(p.getUniqueId()) && d.getPetType().equals(type)) return d.getLevel();
        }
        LevelData d = new LevelData(p.getUniqueId(), type, Pet.getInstance().getDatabase().getPetLevel(p.getUniqueId(), type));
        levelCache.add(d);
        return d.getLevel();
    }
    public void setPetLevel(Player p, String type, int amount) {

        for(LevelData d : levelCache) {
            if(d.getUuid().equals(p.getUniqueId()) && d.getPetType().equals(type)) {
                levelCache.remove(d);
            }
        }

        Pet.getInstance().getDatabase().setPetLevel(p.getUniqueId(), type, amount);
    }

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

    private @Data class LevelData {
        private UUID uuid;
        private String petType;
        private int level;

        public LevelData(UUID uuid, String petType, int level) {
            this.petType = petType;
            this.uuid = uuid;
            this.level = level;
        }

    }

}
