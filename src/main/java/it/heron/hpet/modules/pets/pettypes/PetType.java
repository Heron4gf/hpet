package it.heron.hpet.modules.pets.pettypes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public interface PetType {

    String getName();

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

}
