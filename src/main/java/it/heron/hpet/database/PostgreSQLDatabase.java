package it.heron.hpet.database;

import org.bukkit.plugin.java.JavaPlugin;

public class PostgreSQLDatabase extends AuthDatabase {

    public PostgreSQLDatabase(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    protected String getDriverName() {
        return "postgresql";
    }
}
