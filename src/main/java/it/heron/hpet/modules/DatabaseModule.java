package it.heron.hpet.modules;

import it.heron.hpet.database.*;
import it.heron.hpet.modules.abstracts.DefaultInstanceModule;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Dictionary;
import java.util.Hashtable;

public class DatabaseModule extends DefaultInstanceModule {

    @Getter
    private AbstractDatabase database;

    /**
     * Constructs a DatabaseModule with the specified plugin instance.
     *
     * @param plugin the JavaPlugin instance associated with this module
     */
    public DatabaseModule(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Returns the name of this module.
     *
     * @return the string "Database"
     */
    @Override
    public String name() {
        return "Database";
    }

    @Override
    protected void onLoad() {
        loadDatabase();
    }

    /**
     * Unloads the current database connection when the module is unloaded.
     */
    @Override
    protected void onUnload() {
        unloadDatabase();
    }


    /**
     * Creates a dictionary mapping database type names to their corresponding database implementation instances.
     *
     * @return a dictionary with keys as database type strings and values as instantiated {@link AbstractDatabase} objects
     */
    private Dictionary<String, AbstractDatabase> databaseDictionary() {
        Dictionary<String, AbstractDatabase> databaseDictionary = new Hashtable<>();
        databaseDictionary.put("mysql", new MySQLDatabase(plugin));
        databaseDictionary.put("mariadb", new MariaDBDatabase(plugin));
        databaseDictionary.put("postgre", new PostgreSQLDatabase(plugin));
        databaseDictionary.put("sqlite", new SQLiteDatabase(plugin));
        return databaseDictionary;
    }

    /**
     * Initializes and loads the database connection based on the configured database type.
     *
     * Retrieves the database type from the plugin configuration, selects the corresponding
     * database implementation, assigns it to the internal field, and invokes its load method.
     */
    private void loadDatabase() {
        String databaseType = plugin.getConfig().getString("database.type").toLowerCase();
        this.database = databaseDictionary().get(databaseType);
        this.database.load();
    }

    /**
     * Unloads the current database by invoking its unload method.
     */
    private void unloadDatabase() {
        this.database.unload();
    }
}
