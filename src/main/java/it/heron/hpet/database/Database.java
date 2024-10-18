package it.heron.hpet.database;

import it.heron.hpet.pettypes.PetType;
import it.heron.hpet.userpets.UnspawnedUserPet;
import it.heron.hpet.userpets.UserPet;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public interface Database {
    void load();
    void close();
    int getPetLevel(UUID uuid, PetType petType);
    void setPetLevel(UUID uuid, PetType petType, int level);
    Set<UnspawnedUserPet> offlineUserPets(OfflinePlayer offlinePlayer);
    void savePet(UserPet userPet);
    void wipeLastPets(Player player);
    void wipePetLevel(Player player, PetType petType);


}
