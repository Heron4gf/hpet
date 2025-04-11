package it.heron.hpet.database;

import org.bukkit.plugin.java.JavaPlugin;

public class SQLiteDatabase extends AbstractDatabase {

    @Override
    protected String getDatabaseUrl() {
        return "jdbc:sqlite:plugins/HeronPets/data.db";
    }
}
