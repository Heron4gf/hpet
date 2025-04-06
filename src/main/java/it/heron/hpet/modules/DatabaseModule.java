package it.heron.hpet.modules;

import it.heron.hpet.database.Database;
import it.heron.hpet.database.redisdatabase.RedisDatabase;
import it.heron.hpet.database.sqldatabases.MariaDB;
import it.heron.hpet.database.sqldatabases.MySQL;
import it.heron.hpet.database.sqldatabases.SQLite;
import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.abstracts.DefaultInstanceModule;
import lombok.Getter;

import java.util.Dictionary;
import java.util.Hashtable;

public class DatabaseModule extends DefaultInstanceModule {

    @Getter
    private Database database;

    @Override
    public String name() {
        return "Database";
    }

    @Override
    protected void onLoad() {
        loadDatabase();
    }

    @Override
    protected void onUnload() {
        unloadDatabase();
    }


    private Dictionary<String, Database> databaseDictionary() {
        Dictionary<String, Database> databaseDictionary = new Hashtable<>();
        PetPlugin petPlugin = (PetPlugin)plugin;
        databaseDictionary.put("mysql", new MySQL(petPlugin));
        databaseDictionary.put("mariadb", new MariaDB(petPlugin));
        databaseDictionary.put("redis", new RedisDatabase(petPlugin));
        databaseDictionary.put("sqlite", new SQLite(petPlugin));
        return databaseDictionary;
    }

    private void loadDatabase() {
        String databaseType = plugin.getConfig().getString("database.type").toLowerCase();
        this.database = databaseDictionary().get(databaseType);
        this.database.load();
    }

    private void unloadDatabase() {
        this.database.close();
    }
}
