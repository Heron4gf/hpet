package it.heron.hpet.database;

import com.j256.ormlite.support.ConnectionSource;

public interface Database {
    /**
 * Initializes or loads the database, preparing it for use.
 */
void load();
    /**
 * Releases resources and disconnects from the database.
 */
void unload();
    /**
 * Returns the connection source used to access the database.
 *
 * @return the ORMLite ConnectionSource for database operations
 */
ConnectionSource getConnectionSource();
}
