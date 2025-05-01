package it.heron.hpet.database;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AuthDatabase extends AbstractDatabase {

    protected final String host;
    protected final int port;
    protected final String databaseName;
    protected final String username;
    protected final String password;

    /**
     * Initializes the database connection parameters by loading them from the configuration of the provided JavaPlugin.
     *
     * @param plugin the JavaPlugin whose configuration contains the database connection settings
     */
    public AuthDatabase(JavaPlugin plugin) {
        this(plugin.getConfig());
    }

    /**
     * Initializes the database connection parameters from the provided configuration.
     *
     * @param config the configuration containing database connection properties
     */
    protected AuthDatabase(FileConfiguration config) {
        this(config.getString("database.host"), config.getInt("database.port"), config.getString("database.name"), config.getString("database.user"), config.getString("database.password"));
    }

    /**
     * Initializes the database connection parameters with the specified host, port, database name, username, and password.
     *
     * @param host the database server host
     * @param port the database server port
     * @param databaseName the name of the database
     * @param username the username for authentication
     * @param password the password for authentication
     */
    protected AuthDatabase(String host, int port, String databaseName, String username, String password) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    /****
 * Returns the name of the database driver to be used for constructing the JDBC URL.
 *
 * @return the database driver name (e.g., "mysql", "postgresql")
 */
protected abstract String getDriverName(); /**
     * Constructs and returns the JDBC connection URL for the configured database.
     *
     * @return the JDBC URL string including driver, host, port, database name, username, and password
     */

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
