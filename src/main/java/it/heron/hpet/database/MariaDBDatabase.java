package it.heron.hpet.database;

import org.bukkit.plugin.java.JavaPlugin;

public class MariaDBDatabase extends AuthDatabase {

    public MariaDBDatabase(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    protected String getDriverName() {
        return "mariadb";
    }
}
