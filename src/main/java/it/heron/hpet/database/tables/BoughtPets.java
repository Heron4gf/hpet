package it.heron.hpet.database.tables;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.DatabaseModule;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@DatabaseTable(tableName = "BoughtPets")
@Data
@NoArgsConstructor
public class BoughtPets {

    @DatabaseField(generatedId = true)
    int transactionId;

    @DatabaseField(canBeNull = false)
    private UUID owner;

    @DatabaseField(canBeNull = false)
    private String petType;

    /**
     * Persists the current BoughtPets instance to the database, inserting or updating as needed.
     *
     * If a record with the same primary key exists, it is updated; otherwise, a new record is created.
     */
    public void save() {
        try {
            DatabaseModule module = (DatabaseModule) PetPlugin.getInstance().getModulesHandler().moduleByName("database");
            Dao<BoughtPets, Integer> dao = DaoManager.createDao(module.getDatabase().getConnectionSource(), BoughtPets.class);
            dao.createOrUpdate(this); // This will insert or update the entry based on the object’s ID
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a BoughtPets record matching the specified owner UUID and pet type from the database.
     *
     * @param owner the UUID of the pet owner
     * @param petType the type of pet to search for
     * @return the corresponding BoughtPets instance if found, or null if no match exists
     */
    public static BoughtPets load(UUID owner, String petType) {
        try {
            DatabaseModule module = (DatabaseModule) PetPlugin.getInstance().getModulesHandler().moduleByName("database");
            Dao<BoughtPets, Integer> dao = DaoManager.createDao(module.getDatabase().getConnectionSource(), BoughtPets.class);

            // Querying the database for the specific entry
            return dao.queryBuilder()
                    .where()
                    .eq("owner", owner)
                    .and()
                    .eq("petType", petType)
                    .queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Determines whether the specified owner has bought a pet of the given type.
     *
     * @param owner the UUID of the pet owner
     * @param petType the type of pet to check
     * @return true if a matching purchase record exists; false otherwise
     */
    public static boolean hasBought(UUID owner, String petType) {
        return load(owner, petType) != null;
    }
}
