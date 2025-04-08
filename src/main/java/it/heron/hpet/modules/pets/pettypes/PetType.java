package it.heron.hpet.modules.pets.pettypes;

import it.heron.hpet.modules.abilities.abstracts.Ability;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;

public interface PetType {

    String getName();

    Component getDisplayName();
    void setDisplayName(Component component);

    List<Component> getDescription();
    void setDescription(List<Component> components);

    Vector getRelativeLocation();
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
    boolean bought(Player player);
    boolean canBuy(Player player);

    Ability getAbility();

}
