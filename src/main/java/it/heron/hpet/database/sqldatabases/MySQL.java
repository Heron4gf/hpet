package it.heron.hpet.database.sqldatabases;

import it.heron.hpet.main.PetPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public final class MySQL extends SQLDatabase {
    public MySQL(PetPlugin instance) {
        super(instance);
    }

    @Override
    public String LastPetTable() {
        return
                "CREATE TABLE IF NOT EXISTS LastPet (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "owner varchar(36) NOT NULL," +
                        "type varchar(32)," +
                        "child boolean," +
                        "glow boolean," +
                        "particle varchar(32)," +
                        "name varchar(32)" +
                        ");";
    }

    public Connection getSQLConnection() {
        try {
            if(this.connection!=null&&!connection.isClosed()){
                return connection;
            }
            //Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://"+ PetPlugin.getInstance().getConfig().getString("mysql.address")+":"+ PetPlugin.getInstance().getConfig().getInt("mysql.port")+"/"+ PetPlugin.getInstance().getConfig().getString("mysql.name")+"?autoReconnect=true", PetPlugin.getInstance().getConfig().getString("mysql.user"), PetPlugin.getInstance().getConfig().getString("mysql.password"));
            return connection;
        } catch (SQLException ex) {
            PLUGIN.getLogger().log(Level.SEVERE,"MYSQL exception on initialize", ex);
        }
        return null;
    }
}
