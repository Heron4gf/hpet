package it.heron.hpet.database;

import org.bukkit.plugin.java.JavaPlugin;

public class MariaDBDatabase extends AuthDatabase {

    /**
     * Creates a new MariaDBDatabase instance using the specified plugin.
     *
     * @param plugin the JavaPlugin instance associated with this database
     */
    public MariaDBDatabase(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Returns the name of the database driver used for MariaDB connections.
     *
     * @return the string "mariadb"
     */
    @Override
    protected String getDriverName() {
        return "mariadb";
    }
}
