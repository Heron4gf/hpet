package it.heron.hpet.database;

import it.heron.hpet.Pet;
import it.heron.hpet.userpets.UnspawnedUserPet;
import it.heron.hpet.userpets.UserPet;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class RedisDatabase extends PetDatabase {

    private JedisPool jedisPool;

    public RedisDatabase(Pet instance) {
        super(instance);

        jedisPool = new JedisPool(Pet.getInstance().getConfig().getString("redis.address"), Pet.getInstance().getConfig().getInt("redis.port"));
    }

    @Override
    public Connection getSQLConnection() {
        return null;
    }

    @Override
    public void load() {
        // do nothing
    }

    @Override
    public String getAllPetLevels(UUID uuid) {
        try(Jedis jedis = jedisPool.getResource()) {
            return jedis.get("hpet.levels."+uuid.toString());
        } catch(Exception ignored) {
            return null;
        }
    }

    @Override
    public void setLevel(UUID uuid, String all) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.set("hpet.levels."+uuid.toString(), all);
        } catch(Exception ignored) {}
    }

    @Override
    public List<Integer> getOfflinePetIDs(OfflinePlayer p) {
        List<Integer> ids = new LinkedList<>();
        try(Jedis jedis = jedisPool.getResource()) {
            for(String id : jedis.smembers("hpet.offlinepetsid."+p.getUniqueId().toString())) {
                ids.add(Integer.parseInt(id));
            }
        } catch(Exception ignored) {}
        return ids;
    }

    public UnspawnedUserPet getUnspawnedPetFromDatabaseId(int id) {
        try(Jedis jedis = jedisPool.getResource()) {
            String pet = jedis.get("hpet.offlinepets."+id);
            if(pet == null) {
                return null;
            }
            String[] split = pet.split(";");

            if(split.length < 6) {
                String[] oldSplit = split;
                split = new String[6];
                for(int i = 0; i < oldSplit.length; i++) {
                    split[i] = oldSplit[i];
                }
                for(int i = oldSplit.length; i < 6; i++) {
                    split[i] = "";
                }
            }

            UnspawnedUserPet unspawnedUserPet = new UnspawnedUserPet(
                    Pet.getPetTypeByName(split[0]),
                    UUID.fromString(split[1]),
                    split[2].equals("true"),
                    split[3],
                    split[4].equals("true"),
                    split[5].equals("null") ? null : Color.fromRGB(Integer.parseInt(split[5])));
            return unspawnedUserPet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.set("hpet.offlinepets."+userPet.getId(), userPet.getType().getName()+";"+userPet.getOwner()+";"+(userPet.getChild() != null)+";"+name+";"+userPet.isGlow()+";"+userPet.getColor().asRGB());
            jedis.sadd("hpet.offlinepetsid."+userPet.getOwner(), userPet.getId()+"");
        } catch(Exception ignored) {}
    }

    @Override
    public void wipePets(Player player) {
        try(Jedis jedis = jedisPool.getResource()) {
            for(int id : getOfflinePetIDs(player)) {
                jedis.del("hpet.offlinepets."+id);
            }
            jedis.del("hpet.offlinepetsid."+player.getUniqueId().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
