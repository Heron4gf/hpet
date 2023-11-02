package it.heron.hpet.database;

import it.heron.hpet.Pet;
import it.heron.hpet.database.cachedresult.CachedResult;
import it.heron.hpet.database.cachedresult.Row;
import it.heron.hpet.pettypes.PetType;
import it.heron.hpet.userpets.UnspawnedUserPet;
import it.heron.hpet.userpets.UserPet;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public abstract class PetDatabase extends Database{

    public PetDatabase(Pet instance) {
        super(instance);
    }


    @Override
    public String getAllPetLevels(UUID uuid) {
        try {
            return this.executeQuery("SELECT * FROM Levels WHERE player=?",uuid+"").getString("data");
        } catch(Exception ignored) {
            return null;
        }
    }

    @Override
    public int getPetLevel(UUID uuid, String petType) {
        try {
            return Integer.parseInt(getAllPetLevels(uuid).split(petType)[1].split(";")[0]);
        } catch(Exception ignored) {}
        return 0;
    }

    @Override
    public void setPetLevel(UUID uuid, String petType, int level) {
        String all = getAllPetLevels(uuid);
        if(all == null) {
            all = "";
        }
        if(all.contains(petType)) {
            all = all.replaceFirst(petType+getPetLevel(uuid, petType), petType+level);
        } else {
            all = all+petType+level+";";
        }
        setLevel(uuid, all);
    }

    @Override
    public void setLevel(UUID uuid, String all) {
        this.executeQuery("REPLACE INTO Levels (player,data) VALUES (?,?)",uuid+"",all);
    }

    @Override
    public List<Integer> getOfflinePetIDs(OfflinePlayer p) {
        List<Integer> ids = new LinkedList<>();
        CachedResult result = this.executeQuery("SELECT * FROM LastPet WHERE owner=?",p.getUniqueId()+"");
        while(result.next()) {
            ids.add(result.getInt("id"));
        }
        return ids;
    }

    public UnspawnedUserPet getUnspawnedPetFromDatabaseId(int id) {
        try {
            CachedResult result = this.executeQuery("SELECT * FROM LastPet WHERE id=?","INT:"+id);
            UnspawnedUserPet unspawnedUserPet = new UnspawnedUserPet(
                    Pet.getPetTypeByName(result.getString("type")),
                    UUID.fromString(result.getString("owner")),
                    result.getInt("child")==1,
                    result.getString("name"),
                    result.getInt("glow")==1);
            return unspawnedUserPet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<UnspawnedUserPet> getUnspawnedPets(OfflinePlayer player) {
        List<UnspawnedUserPet> unspawnedUserPets = new LinkedList<>();
        try {
            for(int id : getOfflinePetIDs(player)) {
                unspawnedUserPets.add(getUnspawnedPetFromDatabaseId(id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unspawnedUserPets;
    }

    @Override
    public void savePet(UserPet userPet) {
        String particle = "";
        if(userPet.getParticle() != null) {
            particle = userPet.getParticle().getParticle().name();
        }
        String name = "";
        if(userPet.getName() != null) {
            name = userPet.getName();
        }
        this.executeQuery("INSERT INTO LastPet (owner,type,glow,particle,child,name) VALUES (?,?,?,?,?,?)",
                userPet.getOwner()+"",
                userPet.getType().getName(),
                "INT:"+fromBool(userPet.isGlow()),
                particle,
                "INT:"+fromBool(userPet.getChild()!=null),
                name);
    }

    private int fromBool(boolean b) {
        if(b) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public void wipePets(Player player) {
        this.executeQuery("DELETE FROM LastPet WHERE owner=?",player.getUniqueId()+"");
    }
}
