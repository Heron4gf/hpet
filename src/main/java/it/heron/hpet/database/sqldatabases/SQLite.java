package it.heron.hpet.database.sqldatabases;


import it.heron.hpet.main.PetPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;


public final class SQLite extends SQLDatabase {
    private String dbname;
    public SQLite(PetPlugin instance){
        super(instance);
        dbname = "database";
    }

    @Override
    public String LastPetTable() {
        return
                "CREATE TABLE IF NOT EXISTS LastPet (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "owner varchar(36) NOT NULL," +
                        "type varchar(32)," +
                        "child INTEGER," +
                        "glow INTEGER," +
                        "particle varchar(32)," +
                        "name varchar(32)" +
                        ");";
    }

    @Override
    public String LevelsTable() {
        return
                "CREATE TABLE IF NOT EXISTS PetLevels (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "player TEXT NOT NULL," +
                        "petType TEXT NOT NULL," +
                        "level INTEGER" +
                        ");";
    }



    public Connection getSQLConnection() {
        File dataFolder = new File(PLUGIN.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                PLUGIN.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
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
            PLUGIN.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            PLUGIN.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

}

