package it.heron.hpet.database;

import org.bukkit.plugin.java.JavaPlugin;

public class MySQLDatabase extends AuthDatabase {
    /**
     * Constructs a MySQLDatabase instance using the provided plugin.
     *
     * @param plugin the JavaPlugin associated with this database
     */
    public MySQLDatabase(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Returns the name of the database driver used by this class.
     *
     * @return the string "mysql"
     */
    @Override
    protected String getDriverName() {
        return "mysql";
    }
}
