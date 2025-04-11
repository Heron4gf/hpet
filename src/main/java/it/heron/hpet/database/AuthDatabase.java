package it.heron.hpet.database;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AuthDatabase extends AbstractDatabase {

    protected final String host;
    protected final int port;
    protected final String databaseName;
    protected final String username;
    protected final String password;

    public AuthDatabase(JavaPlugin plugin) {
        this(plugin.getConfig());
    }

    protected AuthDatabase(FileConfiguration config) {
        this(config.getString("database.host"), config.getInt("database.port"), config.getString("database.name"), config.getString("database.user"), config.getString("database.password"));
    }

    protected AuthDatabase(String host, int port, String databaseName, String username, String password) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    protected abstract String getDriverName(); // e.g., "mysql", "postgresql"

    @Override
    protected String getDatabaseUrl() {
        return String.format(
                "jdbc:%s://%s:%d/%s?user=%s&password=%s",
                getDriverName(),
                host,
                port,
                databaseName,
                username,
                password
        );
    }
}
