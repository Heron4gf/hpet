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

    protected abstract String getDatabaseUrl();

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
