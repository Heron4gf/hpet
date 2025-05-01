package it.heron.hpet.placeholders;

import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import it.heron.hpet.main.PetPlugin;

public class PlaceholdersExtension extends PlaceholderExpansion {

    /**
     * Returns the identifier string used for this placeholder expansion.
     *
     * @return the placeholder identifier "hpet"
     */
    @Override
    public @NotNull String getIdentifier() {
        return "hpet";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Heron4gf";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * Returns the current version of the plugin.
     *
     * @return the plugin version string
     */
    @Override
    public @NotNull String getVersion() {
        return PetPlugin.getInstance().getDescription().getVersion();
    }

    /**
     * Handles placeholder requests for player pet information.
     *
     * Returns specific pet-related data based on the provided identifier for an online player.
     * Supported identifiers are:
     * <ul>
     *   <li><b>isSelected</b>: Returns "true" if the player has a selected pet, "false" otherwise.</li>
     *   <li><b>name</b>: Returns the pet type's name.</li>
     *   <li><b>displayname</b>: Returns the insertion-formatted display name of the pet type.</li>
     *   <li><b>level</b>: Returns the pet's level as a string.</li>
     * </ul>
     * If the player is offline, returns an error message. For unsupported identifiers, returns "Invalid placeholder".
     *
     * @param player the player for whom the placeholder is requested
     * @param identifier the placeholder identifier
     * @return the corresponding placeholder value, an error message if the player is offline, or "Invalid placeholder" for unknown identifiers
     */
    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        UserPet userPet;
        if(player.isOnline()) {
            userPet = PetPlugin.getApi().userPet(player.getPlayer());
        } else {
            return "Can't retrieve pet data of a offline player";
        }
        if(identifier.equalsIgnoreCase("isSelected")) {
            return (userPet != null)+"";
        }
        if(userPet == null) return "";
        switch(identifier) {
            case "name":
                return userPet.getPetType().getName();
            case "displayname":
                return userPet.getPetType().getDisplayName().insertion();
            case "level":
                return userPet.getLevel()+"";
        }
        return "Invalid placeholder";
    }
}
