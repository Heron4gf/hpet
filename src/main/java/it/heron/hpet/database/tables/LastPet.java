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

    public void save() {
        try {
            DatabaseModule module = (DatabaseModule) PetPlugin.getInstance().getModulesHandler().moduleByName("database");
            Dao<LastPet, UUID> dao = DaoManager.createDao(module.getDatabase().getConnectionSource(), LastPet.class);
            dao.createOrUpdate(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
