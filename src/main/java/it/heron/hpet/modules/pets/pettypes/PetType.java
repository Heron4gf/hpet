package it.heron.hpet.modules.pets.pettypes;

import it.heron.hpet.modules.abilities.abstracts.Ability;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;

public interface PetType {

    /**
 * Returns the internal name identifier for the pet type.
 *
 * @return the pet type's internal name
 */
String getName();

    /**
 * Retrieves the display name of the pet as a formatted component.
 *
 * @return the pet's display name
 */
Component getDisplayName();
    /**
 * Sets the display name of the pet using the specified component.
 *
 * @param component the component representing the new display name
 */
void setDisplayName(Component component);

    /**
 * Retrieves the pet type's description as a list of components.
 *
 * @return a list of components representing the pet's description
 */
List<Component> getDescription();
    /**
 * Sets the description of the pet type using a list of components.
 *
 * @param components the components representing the pet's description
 */
void setDescription(List<Component> components);

    /**
 * Returns the relative location of the pet as a vector.
 *
 * @return the pet's relative position
 */
Vector getRelativeLocation();
    /**
 * Sets the relative location of the pet.
 *
 * @param vector the new relative location vector for the pet
 */
void setRelativeLocation(Vector vector);

    Vector getNametagRelativeLocation();
    void setNametagRelativeLocation(Vector vector);

    Double getPrice();
    void setPrice(Double price);

    String getAnimationName();
    void setAnimationName(String string);

    ItemStack generateGuiIcon(Player viewer);

    boolean isUnlocked(Player player);
    boolean canSee(Player player);
    /**
 * Checks if the specified player has purchased this pet type.
 *
 * @param player the player to check
 * @return true if the player has bought the pet, false otherwise
 */
boolean bought(Player player);
    /**
 * Determines whether the specified player is eligible to purchase this pet type.
 *
 * @param player the player to check eligibility for
 * @return true if the player can buy the pet, false otherwise
 */
boolean canBuy(Player player);

    /**
 * Returns the ability associated with this pet type.
 *
 * @return the pet's associated Ability object
 */
Ability getAbility();

}
