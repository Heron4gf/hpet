package it.heron.hpet.database;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import it.heron.hpet.database.tables.LastPet;
import it.heron.hpet.database.tables.PetLevel;
import lombok.Getter;

public abstract class AbstractDatabase implements Database {

    @Getter
    protected ConnectionSource connectionSource;

    /**
 * Returns the JDBC database URL to be used for establishing the connection.
 *
 * @return the database connection URL string
 */
protected abstract String getDatabaseUrl();

    /**
     * Initializes the database connection and ensures required tables exist.
     *
     * Establishes a JDBC connection using the URL provided by {@code getDatabaseUrl()}, and creates the {@code LastPet} and {@code PetLevel} tables if they do not already exist.
     */
    @Override
    public void load() {
        try {
            connectionSource = new JdbcConnectionSource(getDatabaseUrl());
            // Register table classes
            TableUtils.createTableIfNotExists(connectionSource, LastPet.class);
            TableUtils.createTableIfNotExists(connectionSource, PetLevel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the database connection source if it is open.
     *
     * Ensures that any resources associated with the database connection are released.
     */
    @Override
    public void unload() {
        try {
            if (connectionSource != null) {
                connectionSource.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
