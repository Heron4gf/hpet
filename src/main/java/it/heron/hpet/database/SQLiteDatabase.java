package it.heron.hpet.database;

import org.bukkit.plugin.java.JavaPlugin;

public class SQLiteDatabase extends AbstractDatabase {

    private final JavaPlugin plugin;

    /**
     * Creates a new SQLiteDatabase instance associated with the specified plugin.
     *
     * @param plugin the JavaPlugin whose data folder will be used for the SQLite database file
     */
    public SQLiteDatabase(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns the JDBC URL for connecting to the SQLite database file located in the plugin's data folder.
     *
     * @return the SQLite JDBC connection URL for the plugin's data.db file
     */
    @Override
    protected String getDatabaseUrl() {
        return "jdbc:sqlite:" + plugin.getDataFolder() + "/data.db";
    }
}
