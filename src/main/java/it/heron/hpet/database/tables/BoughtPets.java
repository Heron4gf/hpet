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
     * Save or update the current instance to the database.
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
     * Load a BoughtPet entry from the database by owner UUID and pet type.
     *
     * @param owner The owner’s UUID.
     * @param petType The type of pet.
     * @return The matching BoughtPets entry or null if not found.
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
     * Check if a pet has already been bought by the specified owner.
     *
     * @param owner The owner’s UUID.
     * @param petType The type of pet.
     * @return True if the pet has been bought, otherwise false.
     */
    public static boolean hasBought(UUID owner, String petType) {
        return load(owner, petType) != null;
    }
}
