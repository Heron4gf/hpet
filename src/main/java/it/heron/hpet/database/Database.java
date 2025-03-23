package it.heron.hpet.database;

import it.heron.hpet.ChildPet;
import it.heron.hpet.Pet;
import it.heron.hpet.Utils;
import it.heron.hpet.userpets.UserPet;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;


public abstract class Database {
    Pet plugin;
    Connection connection;
    @Getter
    public String table = "lastpet";
    public Database(Pet instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public String SQLiteCreateLastpetTable = "CREATE TABLE IF NOT EXISTS lastpet (" +
            "`player` varchar(36) NOT NULL," +
            "`type` varchar(32)," +
            "`child` boolean," +
            "`glow` boolean," +
            "`particle` varchar(32)," +
            "`name` varchar(32)," +
            "PRIMARY KEY (`player`)" +
            ");";

    public String SQLiteCreateLevelTable = "CREATE TABLE IF NOT EXISTS level (" +
            "`player` varchar(36) NOT NULL," +
            "`data` varchar(300)," +
            "PRIMARY KEY (`player`)" +
            ");";

    public void load() {
        //Utils.runAsync(() -> {
            connection = getSQLConnection();
            try {
                Statement s = connection.createStatement();
                s.executeUpdate(SQLiteCreateLastpetTable);
                s.executeUpdate(SQLiteCreateLevelTable);
                s.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            initialize();
        //});
    }

    public void initialize(){
        //Utils.runAsync(() -> {
            connection = getSQLConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE player = ?");
                ps.setString(1, "player");
                ResultSet rs = ps.executeQuery();
                close(ps, rs);

            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
            }
        //});
    }

    public String getType(UUID uuid) {
        return getStringData(uuid, "type", table);
    }

    public String getAllPetLevels(UUID uuid) { return getStringData(uuid, "data", "level");}
    public int getPetLevel(UUID uuid, String petType) {
        try {
            return Integer.parseInt(getAllPetLevels(uuid).split(petType)[1].split(";")[0]);
        } catch(Exception ignored) {}
        return 0;
    }
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
    public void setLevel(UUID uuid, String all) {
        Utils.runAsync(() -> {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = getSQLConnection();
                ps = conn.prepareStatement("REPLACE INTO " + "level" + " (player,data) VALUES(?,?)");

                ps.setString(1, uuid + "");
                ps.setString(2, all);

                ps.executeUpdate();
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
        });
    }

    public String getStringData(UUID uuid, String pos, String t) {
        String string = uuid+"";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + t + " WHERE player = '"+string+"';");

            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("player").equalsIgnoreCase(string.toLowerCase())){ // Tell database to search for the player you sent into the method. e.g getTokens(sam) It will look for sam.
                    return rs.getString(pos); // Return the players ammount of kills. If you wanted to get total (just a random number for an example for you guys) You would change this to total!
                }
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

    public UserPet getOfflinePet(OfflinePlayer p) {
        return getOfflinePet(p.getUniqueId(), false);
    }
    public UserPet getOfflinePet(UUID uuid, boolean setOwner) {
        String string = uuid+"";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE player = '"+string+"';");

            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("player").equalsIgnoreCase(string.toLowerCase())){
                    try {
                        Player owner = null;
                        if(setOwner) owner = Bukkit.getPlayer(uuid);

                        Pet.getApi().selectPet(owner, rs.getString("type"));
                        UserPet upet = Pet.getApi().getUserPet(owner);
                        upet.setGlow(rs.getBoolean("glow"));
                        upet.setName(rs.getString("name"));
                        if(rs.getBoolean("trail")) upet.setChild(new ChildPet());
                        return upet;
                    } catch(Exception ignored) {}
                    return null;

                }
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

    public void setData(UUID uuid, String type, boolean child, boolean glow, String particle, String name) {
        //Utils.runAsync(() -> {


            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = getSQLConnection();
                ps = conn.prepareStatement("REPLACE INTO " + table + " (player,type,child,glow,particle,name) VALUES(?,?,?,?,?,?)"); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.

                ps.setString(1, uuid + "");                                             // YOU MUST put these into this line!! And depending on how many
                ps.setString(2, type);
                ps.setBoolean(3, child);
                ps.setBoolean(4, glow);
                ps.setString(5, particle);
                ps.setString(6, name);

                ps.executeUpdate();
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
        //});
    }


    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

