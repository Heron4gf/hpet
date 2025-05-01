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

import java.util.List;
import java.util.UUID;

@DatabaseTable(tableName = "PetLevel") @Data @NoArgsConstructor
public class PetLevel {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private UUID owner;

    @DatabaseField(canBeNull = false)
    private String petType;

    @DatabaseField(canBeNull = false)
    private int level;

    public void save() {
        try {
            DatabaseModule module = (DatabaseModule) PetPlugin.getInstance().getModulesHandler().moduleByName("database");
            Dao<PetLevel, Integer> dao = DaoManager.createDao(module.getDatabase().getConnectionSource(), PetLevel.class);
            dao.createOrUpdate(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PetLevel load(UUID owner, String petType) {
        try {
            DatabaseModule module = (DatabaseModule) PetPlugin.getInstance().getModulesHandler().moduleByName("database");
            Dao<PetLevel, Integer> dao = DaoManager.createDao(module.getDatabase().getConnectionSource(), PetLevel.class);
            List<PetLevel> result = dao.queryBuilder()
                    .where()
                    .eq("owner", owner)
                    .and()
                    .eq("petType", petType)
                    .query();
            return result.isEmpty() ? null : result.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
