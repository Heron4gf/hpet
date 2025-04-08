package it.heron.hpet.database.sqldatabases;

import it.heron.hpet.database.AbstractDatabase;
import it.heron.hpet.database.cachedresult.Row;
import it.heron.hpet.database.cachedresult.paramtype.ParamType;
import it.heron.hpet.groups.HSlot;
import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.database.cachedresult.CachedResult;
import it.heron.hpet.modules.pets.userpets.old.UnspawnedUserPet;
import it.heron.hpet.modules.pets.userpets.old.HeadUserPet;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;

public abstract class SQLDatabase extends AbstractDatabase {
    protected Connection connection;

    @Getter
    private long badQueries = 0;

    public String LastPetTable() {
        return
                "CREATE TABLE IF NOT EXISTS LastPet (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "owner varchar(36) NOT NULL," +
                        "type varchar(32)," +
                        "child INT," +
                        "glow INT," +
                        "particle varchar(32)," +
                        "name varchar(32)" +
                        ");";
    }

    public String LevelsTable() {
        return
                "CREATE TABLE IF NOT EXISTS PetLevels (" +
                        "id INT AUTO_INCREMENT," +
                        "player varchar(36) NOT NULL," +
                        "petType VARCHAR(36) NOT NULL," +
                        "level INT," +
                        "PRIMARY KEY (id)" +
                        ");";
    }

    public SQLDatabase(PetPlugin instance) {
        super(instance);
    }

    public Connection getSQLConnection() {
        return this.connection;
    }

    @Override
    public void load() {
        this.connection = getSQLConnection();
        try {
            Statement statement = connection.createStatement();
            statement.execute(LastPetTable());
            statement.close();
            if (PetPlugin.getInstance().getCachedConfigurationInfo().isPetLevellingEnabled()) {
                statement = connection.createStatement();
                statement.execute(LevelsTable());
                statement.close();
                executeQuery("INSERT INTO PetLevels (player,petType,level) VALUES ('unknown','unknown',0);");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CachedResult executeQuery(String query, String... parameters) {
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(query);

            int i = 1;
            for (String parameter : parameters) {
                ParamType paramType = ParamType.whatParamTypeIsThis(parameter);
                if (parameter.contains(":")) parameter = parameter.split(":")[1];
                switch (paramType) {
                    case INT:
                        ps.setInt(i, Integer.parseInt(parameter));
                        break;
                    case DOUBLE:
                        ps.setDouble(i, Double.parseDouble(parameter));
                        break;
                    case OBJECT:
                        ps.setObject(i, parameter);
                        break;
                    default:
                        ps.setString(i, parameter);
                        break;
                    case BOOLEAN:
                        ps.setBoolean(i, Boolean.parseBoolean(parameter));
                        break;
                }
                i++;
            }

        if (query.startsWith("SELECT")) {
            rs = ps.executeQuery();

            CachedResult cachedResult = new CachedResult();
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Row row = new Row();
                for (int j = 1; j <= columnCount; j++) {
                    row.addValue(rs.getMetaData().getColumnName(j), rs.getObject(j));
                }
                cachedResult.addRow(row);
            }
            return cachedResult;
        } else {
            ps.execute();
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    } finally {
        try {
            if (ps != null)
                ps.close();
            if (conn != null)
                conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    return null;
}

    @Override
    public int getPetLevel(UUID uuid, OldPetType petType) {
        if(!PetPlugin.getInstance().getCachedConfigurationInfo().isPetLevellingEnabled()) return -1;
        try {
            String name = petType.getName();
            CachedResult cachedResult = executeQuery("SELECT level FROM PetLevels WHERE player = ? AND petType = ?;", uuid.toString(), name);
            int level = cachedResult.getInt("level");
            badQueries = 0;
            return level;
        } catch (Exception ignored) {
            badQueries++;
            if(badQueries > 5) {
                return -1;
            }
            return getPetLevel(uuid, new NoPetType());
        }
    }

    @Override
    public void setPetLevel(UUID uuid, OldPetType petType, int level) {
        if(!PetPlugin.getInstance().getCachedConfigurationInfo().isPetLevellingEnabled()) return;
        String name = petType.getName();
        executeQuery("INSERT INTO PetLevels (player,petType,level) VALUES(?,?,?);", uuid.toString(), name, "INT:"+level);
    }

    @Override
    public Set<UnspawnedUserPet> offlineUserPets(OfflinePlayer offlinePlayer) {
        List<Integer> ids = new LinkedList<>();
        CachedResult result = this.executeQuery("SELECT * FROM LastPet WHERE owner=?",offlinePlayer.getUniqueId()+"");
        while(result.next()) {
            ids.add(result.getInt("id"));
        }

        Set<UnspawnedUserPet> unspawnedUserPets = new HashSet<>();

        try {
            for(int id : ids) {

                CachedResult lastPetResult = this.executeQuery("SELECT * FROM LastPet WHERE id=?","INT:"+id);
                UnspawnedUserPet unspawnedUserPet = new UnspawnedUserPet(
                        PetPlugin.getPetTypeByName(lastPetResult.getString("type")),
                        UUID.fromString(lastPetResult.getString("owner")),
                        lastPetResult.getInt("child")==1,
                        lastPetResult.getString("name"),
                        lastPetResult.getInt("glow")==1);
                unspawnedUserPets.add(unspawnedUserPet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return unspawnedUserPets;
    }

    @Override
    public void savePet(HeadUserPet userPet) {
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
                "INT:"+(userPet.isGlow() ? 1 : 0),
                particle,
                "INT:"+(userPet.getChild()!=null ? 1 : 0),
                name);
    }

    @Override
    public void wipeLastPets(Player player) {
        this.executeQuery("DELETE FROM LastPet WHERE owner=?",player.getUniqueId().toString());
    }

    @Override
    public void wipePetLevel(Player player, OldPetType petType) {
        this.executeQuery("DELETE FROM PetLevels WHERE player=? AND petType=?", player.getUniqueId().toString(), petType.getName());
    }

    @Override
    public void convertToNewerVersion(String oldVersion) {
        long start = System.currentTimeMillis();
        if(!oldVersion.equals("4.6")) {
            return;
        }

        CachedResult all = executeQuery("SELECT * FROM Levels");
        while(all.next()) {
            try {
                UUID uuid = UUID.fromString(all.getString("player"));
                String allLevels = all.getString("data");

                String[] splitted = allLevels.split(";");
                for(String s : splitted) {
                    for(HSlot hslot : PetPlugin.getInstance().getPetTypes()) {
                        if(s.contains(hslot.getName())) {
                            int level = Integer.parseInt(s.split(hslot.getName())[1]);
                            executeQuery("INSERT INTO PetLevels (player,petType,level) VALUES (?,?,?)", uuid.toString(), hslot.getName(), "INT:"+level);
                            break;
                        }
                    }
                }

            } catch (Exception exception) {
                Bukkit.getLogger().warning("There was an error while converting an user levels data to the newer database table");
                exception.printStackTrace();
            }
        }

        float secondsPassed = (System.currentTimeMillis()-start)/1000f;
        Bukkit.getLogger().info("Levels database was converted in "+secondsPassed+"s");
    }
}
