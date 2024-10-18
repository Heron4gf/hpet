package it.heron.hpet.main;


import it.heron.hpet.database.databasetype.DatabaseType;
import lombok.Data;
import org.bukkit.configuration.file.FileConfiguration;

public @Data class CachedConfiguration {

    private boolean petLevellingEnabled;
    private DatabaseType databaseType;
    private int maxLevel;


    public CachedConfiguration(PetPlugin petPlugin) {
        FileConfiguration config = petPlugin.getConfig();
        this.petLevellingEnabled = config.getBoolean("level.enable", false);
        this.databaseType = DatabaseType.valueOf(config.getString("database.type", DatabaseType.SQLITE.name()));
        this.maxLevel = config.getInt("level.max", 100);
    }
}
