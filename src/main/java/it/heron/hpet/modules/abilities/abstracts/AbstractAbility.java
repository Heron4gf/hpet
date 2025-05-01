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
     * Constructs an AbstractAbility with a specified execution interval.
     *
     * @param seconds the interval, in seconds, at which the ability should attempt to execute; values less than 0.05 seconds are rounded up to ensure a minimum interval of 50 milliseconds
     */
    public AbstractAbility(float seconds) {
        long ms = Math.max(50, (long) (seconds * 1000)); // Ensure minimum interval (e.g., 1 tick)
        this.runevery = ms;
    }

    /**
     * Determines whether the ability's cooldown period has expired and it is eligible to execute again.
     *
     * @param userPet the pet instance associated with the ability
     * @return true if the cooldown has expired; false otherwise
     */
    protected boolean shouldExecute(UserPet userPet) {
        return System.currentTimeMillis() >= (last_run + runevery);
    }

    /**
     * Executes the ability for the given pet if the owner is online and the cooldown has expired.
     * <p>
     * Identifies the last player who damaged the owner within a specified distance and passes this information,
     * along with a flag indicating if this is the first successful execution, to the ability logic.
     * If an exception occurs during execution, the cooldown state is not updated.
     *
     * @param userPet the pet instance triggering the ability
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
 * Executes the specific ability logic for a pet when triggered.
 *
 * @param userPet   the pet instance this ability is associated with
 * @param owner     the online player who owns the pet
 * @param first_run true if this is the first successful execution of this ability instance
 * @param enemy     the player who most recently damaged the owner within range, or null if not applicable
 */
    public abstract void onExecute(UserPet userPet, Player owner, boolean first_run, Player enemy);

}