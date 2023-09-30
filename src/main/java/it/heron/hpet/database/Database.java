package it.heron.hpet.database;

import it.heron.hpet.ChildPet;
import it.heron.hpet.Pet;
import it.heron.hpet.Utils;
import it.heron.hpet.database.cachedresult.CachedResult;
import it.heron.hpet.database.cachedresult.Row;
import it.heron.hpet.database.paramtype.ParamType;
import it.heron.hpet.userpets.UserPet;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;


public abstract class Database {
    protected Pet plugin;
    protected Connection connection;
    public Database(Pet instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();


    protected String[] queryTables() {
        return new String[]{
                "CREATE TABLE IF NOT EXISTS LastPet (" +
                        "'id' INT AUTO INCREMENT PRIMARY KEY," +
                        "'owner' varchar(36) NOT NULL," +
                        "'type' varchar(32)," +
                        "'child' boolean," +
                        "'glow' boolean," +
                        "'particle' varchar(32)," +
                        "'name' varchar(32),"+
                        ");",
                "CREATE TABLE IF NOT EXISTS Levels (" +
                        "'player' varchar(36) NOT NULL," +
                        "'data' varchar(300)," +
                        "PRIMARY KEY (`player`)" +
                        ");"
        };
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement statement = connection.createStatement();
            for(String queryTable : queryTables()) {
                statement.executeUpdate(queryTable);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
            for(String parameter : parameters) {
                ParamType paramType = ParamType.whatParamTypeIsThis(parameter);
                if(parameter.contains(":")) parameter = parameter.split(":")[1];
                switch(paramType) {
                    case INT:
                        ps.setInt(i,Integer.parseInt(parameter));
                        break;
                    case DOUBLE:
                        ps.setDouble(i,Double.parseDouble(parameter));
                        break;
                    case OBJECT:
                        ps.setObject(i,parameter);
                        break;
                    default:
                        ps.setString(i,parameter);
                        break;
                    case BOOLEAN:
                        ps.setBoolean(i,Boolean.parseBoolean(parameter));
                        break;
                }
                i++;
            }

            if(query.startsWith("SELECT")) {
                rs = ps.executeQuery();

                CachedResult cachedResult = new CachedResult();
                int columnCount = rs.getMetaData().getColumnCount();
                while(rs.next()) {
                    Row row = new Row();
                    for(int j = 1; j <= columnCount; j++) {
                        row.addValue(rs.getMetaData().getColumnName(j),rs.getObject(j));
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

    public abstract String getAllPetLevels(UUID uuid);
    public abstract int getPetLevel(UUID uuid, String petType);
    public abstract void setPetLevel(UUID uuid, String petType, int level);
    public abstract void setLevel(UUID uuid, String all);

    public abstract List<Integer> getOfflinePetIDs(OfflinePlayer p);

    public abstract void savePet(UserPet userPet);
    public abstract void wipePets(Player player);

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

