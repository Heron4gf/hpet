package it.heron.hpet.database;

import org.bukkit.plugin.java.JavaPlugin;

public class PostgreSQLDatabase extends AuthDatabase {

    /**
     * Constructs a PostgreSQLDatabase instance using the provided JavaPlugin.
     *
     * @param plugin the JavaPlugin associated with this database instance
     */
    public PostgreSQLDatabase(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Returns the name of the database driver used by this implementation.
     *
     * @return the string "postgresql"
     */
    @Override
    protected String getDriverName() {
        return "postgresql";
    }
}
