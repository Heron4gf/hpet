package it.heron.hpet.database;

import org.bukkit.plugin.java.JavaPlugin;

public class SQLiteDatabase extends AbstractDatabase {

    private final JavaPlugin plugin;

    public SQLiteDatabase(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected String getDatabaseUrl() {
        return "jdbc:sqlite:" + plugin.getDataFolder() + "/data.db";
    }
}
