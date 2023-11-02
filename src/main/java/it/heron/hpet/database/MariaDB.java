package it.heron.hpet.database;

import it.heron.hpet.Pet;
import it.heron.hpet.userpets.UserPet;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class MariaDB extends PetDatabase {

    @Override
    protected String[] queryTables() {
        return new String[] {
                "CREATE TABLE IF NOT EXISTS LastPet ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "owner VARCHAR(36) NOT NULL,"
                        + "type VARCHAR(32),"
                        + "child INT,"
                        + "glow INT,"
                        + "particle VARCHAR(32),"
                        + "name VARCHAR(32)"
                        + ");",
                "CREATE TABLE IF NOT EXISTS Levels ("
                        + "player VARCHAR(36) NOT NULL,"
                        + "data VARCHAR(300),"
                        + "PRIMARY KEY (player)"
                        + ");"
        };
    }

    public MariaDB(Pet instance) {
        super(instance);
    }

    @Override
    public Connection getSQLConnection() {
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            //Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://" + Pet.getInstance().getConfig().getString("mysql.host")
                    + ":" + Pet.getInstance().getConfig().getInt("mysql.port")
                    + "/" + Pet.getInstance().getConfig().getString("mysql.name")
                    + "?autoReconnect=true", Pet.getInstance().getConfig().getString("mysql.user"), Pet.getInstance().getConfig().getString("mysql.password"));
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"MariaDB exception on initialize", ex);
        }
        return null;
    }

}
