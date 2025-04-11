package it.heron.hpet.modules;

import it.heron.hpet.database.*;
import it.heron.hpet.modules.abstracts.DefaultInstanceModule;
import lombok.Getter;

import java.util.Dictionary;
import java.util.Hashtable;

public class DatabaseModule extends DefaultInstanceModule {

    @Getter
    private AbstractDatabase database;

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


    private Dictionary<String, AbstractDatabase> databaseDictionary() {
        Dictionary<String, AbstractDatabase> databaseDictionary = new Hashtable<>();
        databaseDictionary.put("mysql", new MySQLDatabase(plugin));
        databaseDictionary.put("mariadb", new MariaDBDatabase(plugin));
        databaseDictionary.put("postgre", new PostgreSQLDatabase(plugin));
        databaseDictionary.put("sqlite", new SQLiteDatabase());
        return databaseDictionary;
    }

    private void loadDatabase() {
        String databaseType = plugin.getConfig().getString("database.type").toLowerCase();
        this.database = databaseDictionary().get(databaseType);
        this.database.load();
    }

    private void unloadDatabase() {
        this.database.unload();
    }
}
