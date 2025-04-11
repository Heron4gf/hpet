package it.heron.hpet.database;

import com.j256.ormlite.support.ConnectionSource;

public interface Database {
    void load();
    void unload();
    ConnectionSource getConnectionSource();
}
