package it.heron.hpet.modules.abilities.abstracts;

import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Base implementation for a Pet Ability, handling execution timing and cooldowns.
 * Subclasses must implement the specific ability logic in onExecute.
 */
@Data
public abstract class AbstractAbility implements Ability { // Assuming Ability interface exists

    private static double MAX_ENEMY_DISTANCE = 10d;

    private long last_run = System.currentTimeMillis(); // Timestamp of the last successful execution
    private boolean has_run = false; // Flag indicating if the ability has ever run successfully
    private final long runevery; // The interval (in milliseconds) at which the ability should attempt to run

    /**
     * @param seconds The interval in seconds at which this ability should try to execute.
     */
    public AbstractAbility(float seconds) {
        long ms = Math.max(50, (long) (seconds * 1000)); // Ensure minimum interval (e.g., 1 tick)
        this.runevery = ms;
    }

    /**
     * Checks if enough time has passed since the last execution.
     */
    protected boolean shouldExecute(UserPet userPet) {
        return System.currentTimeMillis() >= (last_run + runevery);
    }

    /**
     * Executes the ability if the conditions (owner online, cooldown expired) are met.
     * Attempts to find the last player who damaged the owner.
     * This method should typically be called periodically (e.g., by a scheduler).
     * @param userPet The pet instance triggering the ability.
     */
    @Override
    public final void execute(UserPet userPet) {
        Player owner = Bukkit.getPlayer(userPet.getOwner());
        if (owner == null || !owner.isOnline()) {
            return; // Owner not found or offline
        }

        if (!shouldExecute(userPet)) {
            return;
        }

        Player enemy = null;
        EntityDamageEvent lastDamageEvent = owner.getLastDamageCause();
        if (lastDamageEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) lastDamageEvent;
            Entity damager = damageByEntityEvent.getDamager();
            if (damager instanceof Player) {
                if(damager.getWorld().equals(owner.getWorld()) && damager.getLocation().distance(owner.getLocation()) < MAX_ENEMY_DISTANCE) {
                    enemy = (Player) damager;
                }
            }
        }

        boolean first_run = !this.has_run;

        try {
            onExecute(userPet, owner, first_run, enemy);
        } catch (Exception e) {
            Bukkit.getLogger().warning("An error occurred while executing an ability for pet "+userPet.getPetType().getName()+" of player "+owner.getName());
            e.printStackTrace();
            return; // Do not update state if execution failed
        }

        this.last_run = System.currentTimeMillis();
        this.has_run = true;
    }

    /**
     * The core logic of the ability, implemented by subclasses.
     * Called by execute() when the owner is online and the cooldown has expired.
     *
     * @param userPet   The pet instance associated with this ability.
     * @param owner     The online Player who owns the pet.
     * @param first_run True if this is the first time this ability instance is executing successfully.
     * @param enemy     The Player who last damaged the owner, if identifiable via getLastDamageCause() and was a Player. Can be null.
     */
    public abstract void onExecute(UserPet userPet, Player owner, boolean first_run, Player enemy);

}