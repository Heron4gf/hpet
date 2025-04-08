package it.heron.hpet.database.redisdatabase;

import it.heron.hpet.database.AbstractDatabase;
import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.pets.userpets.old.UnspawnedUserPet;
import it.heron.hpet.modules.pets.userpets.old.HeadUserPet;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class RedisDatabase extends AbstractDatabase {

    private JedisPool jedisPool;

    public RedisDatabase(PetPlugin instance) {
        super(instance);
        jedisPool = new JedisPool(PetPlugin.getInstance().getConfig().getString("database.host"),
                PetPlugin.getInstance().getConfig().getInt("database.port"));
    }

    @Override
    public void load() {
        // Redis does not require initialization.
    }

    @Override
    public void close() {
        jedisPool.close(); // Close the connection pool when the plugin shuts down.
    }

    @Override
    public int getPetLevel(UUID uuid, OldPetType petType) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "hpet.levels." + uuid.toString() + "." + petType.getName();
            String level = jedis.get(key);
            return level != null ? Integer.parseInt(level) : 0; // Return 0 if no level is found
        } catch (Exception ignored) {
            return 0;
        }
    }

    @Override
    public void setPetLevel(UUID uuid, OldPetType petType, int level) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "hpet.levels." + uuid.toString() + "." + petType.getName();
            jedis.set(key, Integer.toString(level)); // Set pet level
        } catch (Exception ignored) {
            // Handle Redis exception, possibly logging it
        }
    }

    @Override
    public Set<UnspawnedUserPet> offlineUserPets(OfflinePlayer offlinePlayer) {
        Set<UnspawnedUserPet> unspawnedUserPets = new HashSet<>();
        UUID uuid = offlinePlayer.getUniqueId();
        try (Jedis jedis = jedisPool.getResource()) {
            // Fetch all the keys that match the pattern for the user's pets
            Set<String> keys = jedis.keys("hpet.lastpet." + uuid.toString() + ".*");
            for (String key : keys) {
                String[] petData = jedis.hvals(key).toArray(new String[0]);
                if (petData.length > 0) {
                    // Unpack Redis hash values into pet attributes (example: type, glow, etc.)
                    OldPetType type = PetPlugin.getPetTypeByName(petData[0]);
                    boolean glow = Boolean.parseBoolean(petData[1]);
                    String name = petData[2];
                    boolean child = Boolean.parseBoolean(petData[3]);

                    UnspawnedUserPet unspawnedUserPet = new UnspawnedUserPet(type, uuid, child, name, glow);
                    unspawnedUserPets.add(unspawnedUserPet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unspawnedUserPets;
    }

    @Override
    public void savePet(HeadUserPet userPet) {
        String particle = userPet.getParticle() != null ? userPet.getParticle().getParticle().name() : "";
        String name = userPet.getName() != null ? userPet.getName() : "";
        String key = "hpet.lastpet." + userPet.getOwner().toString() + "." + userPet.getType().getName();

        try (Jedis jedis = jedisPool.getResource()) {
            // Save pet data as a Redis hash
            jedis.hset(key, "type", userPet.getType().getName());
            jedis.hset(key, "glow", Boolean.toString(userPet.isGlow()));
            jedis.hset(key, "name", name);
            jedis.hset(key, "child", Boolean.toString(userPet.getChild() != null));
            jedis.hset(key, "particle", particle);
        } catch (Exception ignored) {
            // Handle Redis exception, possibly logging it
        }
    }

    @Override
    public void wipeLastPets(Player player) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Delete all keys for this player's last pets
            Set<String> keys = jedis.keys("hpet.lastpet." + player.getUniqueId().toString() + ".*");
            for (String key : keys) {
                jedis.del(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void wipePetLevel(Player player, OldPetType petType) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "hpet.levels." + player.getUniqueId().toString() + "." + petType.getName();
            jedis.del(key); // Remove the pet level for the specified player and pet type
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void convertToNewerVersion(String oldVersion) {
        if(oldVersion.equals("4.6")) {
            Bukkit.getLogger().warning("Couldn't convert database table from "+oldVersion);
        }
    }
}
