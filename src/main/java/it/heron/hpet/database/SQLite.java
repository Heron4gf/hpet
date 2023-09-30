package it.heron.hpet.database;


import it.heron.hpet.Pet;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;


public class SQLite extends PetDatabase {
    String dbname;
    public SQLite(Pet instance){
        super(instance);
        dbname = "database";
    }

    protected String[] queryTables() {
        return new String[]{
                "CREATE TABLE IF NOT EXISTS LastPet (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "owner varchar(36) NOT NULL," +
                        "type varchar(32)," +
                        "child INTEGER," +
                        "glow INTEGER," +
                        "particle varchar(32)," +
                        "name varchar(32)" +
                        ");",
                "CREATE TABLE IF NOT EXISTS Levels (" +
                        "player varchar(36) NOT NULL," +
                        "data varchar(300)," +
                        "PRIMARY KEY (player)" +
                        ");"
        };
    }


    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

}

