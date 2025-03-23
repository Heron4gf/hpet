package it.heron.hpet.database.sqldatabases;

import it.heron.hpet.database.Database;
import it.heron.hpet.main.PetPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public final class MariaDB extends SQLDatabase {

    public MariaDB(PetPlugin instance) {
        super(instance);
    }

    @Override
    public Connection getSQLConnection() {
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            //Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://" + PetPlugin.getInstance().getConfig().getString("database.host")
                    + ":" + PetPlugin.getInstance().getConfig().getInt("database.port")
                    + "/" + PetPlugin.getInstance().getConfig().getString("database.name")
                    + "?autoReconnect=true", PetPlugin.getInstance().getConfig().getString("database.user"), PetPlugin.getInstance().getConfig().getString("database.password"));
            return connection;
        } catch (SQLException ex) {
            PLUGIN.getLogger().log(Level.SEVERE,"MariaDB exception on initialize", ex);
        }
        return null;
    }

}
