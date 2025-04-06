package it.heron.hpet.database;

import it.heron.hpet.modules.pets.pettypes.OldPetType;
import it.heron.hpet.modules.pets.pettypes.PetType;
import it.heron.hpet.modules.pets.userpets.old.UnspawnedUserPet;
import it.heron.hpet.modules.pets.userpets.old.HeadUserPet;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public interface Database {
    void load();
    void close();
    int getPetLevel(UUID uuid, OldPetType petType);
    void setPetLevel(UUID uuid, OldPetType petType, int level);
    Set<UnspawnedUserPet> offlineUserPets(OfflinePlayer offlinePlayer);
    void savePet(HeadUserPet userPet);
    void wipeLastPets(Player player);
    void wipePetLevel(Player player, OldPetType petType);

    boolean hasBought(Player player, PetType petType);
}
