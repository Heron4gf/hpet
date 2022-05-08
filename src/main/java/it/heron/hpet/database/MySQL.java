package it.heron.hpet.database;

import it.heron.hpet.Pet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class MySQL extends Database {
    public MySQL(Pet instance) {
        super(instance);
    }

    public Connection getSQLConnection() {
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            //Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://"+Pet.getInstance().getConfig().getString("mysql.address")+":"+Pet.getInstance().getConfig().getInt("mysql.port")+"/"+Pet.getInstance().getConfig().getString("mysql.name")+"?autoReconnect=false",Pet.getInstance().getConfig().getString("mysql.user"),Pet.getInstance().getConfig().getString("mysql.password"));
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"MYSQL exception on initialize", ex);
        }
        return null;
    }
}
