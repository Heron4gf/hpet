package it.heron.hpet.database.tables;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import it.heron.hpet.database.AbstractDatabase;
import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.DatabaseModule;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@DatabaseTable(tableName = "LastPet") @NoArgsConstructor @Data
public class LastPet {

    @DatabaseField(id = true)
    private UUID owner;

    @DatabaseField(canBeNull = false)
    private String petType;

    @DatabaseField(canBeNull = true)
    private String petName;

    /**
     * Persists this LastPet instance to the database, creating a new record or updating an existing one.
     */
    public void save() {
        try {
            DatabaseModule module = (DatabaseModule) PetPlugin.getInstance().getModulesHandler().moduleByName("database");
            Dao<LastPet, UUID> dao = DaoManager.createDao(module.getDatabase().getConnectionSource(), LastPet.class);
            dao.createOrUpdate(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the LastPet record associated with the specified owner UUID from the database.
     *
     * @param owner the UUID of the pet owner whose record is to be loaded
     * @return the LastPet object if found; otherwise, null if not found or if an error occurs
     */
    public static LastPet load(UUID owner) {
        try {
            DatabaseModule module = (DatabaseModule) PetPlugin.getInstance().getModulesHandler().moduleByName("database");
            Dao<LastPet, UUID> dao = DaoManager.createDao(module.getDatabase().getConnectionSource(), LastPet.class);
            return dao.queryForId(owner);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
