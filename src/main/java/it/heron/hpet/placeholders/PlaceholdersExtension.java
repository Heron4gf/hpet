package it.heron.hpet.placeholders;

import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import it.heron.hpet.main.PetPlugin;

public class PlaceholdersExtension extends PlaceholderExpansion {

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

    @Override
    public @NotNull String getVersion() {
        return PetPlugin.getInstance().getDescription().getVersion();
    }

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
