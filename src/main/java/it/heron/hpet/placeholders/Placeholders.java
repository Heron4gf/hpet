package it.heron.hpet.placeholders;

import it.heron.hpet.Utils;
import it.heron.hpet.levels.LevelEvents;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import it.heron.hpet.Pet;
import it.heron.hpet.userpets.UserPet;

public class Placeholders extends PlaceholderExpansion {

    //private Pet plugin;

    public Placeholders() { }

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
        return Pet.getInstance().getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer p, String identifier) {
        UserPet pet;
        if(p.isOnline()) {
            pet = Pet.getApi().getUserPet(p.getPlayer());
        } else {
            pet = Pet.getInstance().getDatabase().getOfflinePet(p);
        }
        if(identifier.equalsIgnoreCase("isSelected")) return (pet != null)+"";
        if(pet == null) return "";
        switch(identifier) {
            case "typename":
                return pet.getType().getName();
            case "name":
                return pet.getName();
            case "displayname":
                return pet.getType().getDisplayName();
            case "hasTrail":
                return (pet.getChild() != null)+"";
            case "isGlowing":
                return pet.isGlow()+"";
            case "level":
                return pet.getLevel()+"";
            case "levelup":
                return LevelEvents.getMaxStat(pet)+"";
            case "currentStat":
                return LevelEvents.currentStat(pet)+"";
        }
        if(identifier.startsWith("levelbar_")) {
            int length = Integer.parseInt(identifier.replace("levelbar_", ""));
            // level : max = x : length
            // x = (length * level) / max
            // ■ □
            String s = "";
            int x = (LevelEvents.currentStat(pet)*length)/LevelEvents.getMaxStat(pet);
            for(int i = 0; i < length; i++) {
                if(i <= x) {
                    s = s+"■";
                } else {
                    s = s+"§7□";
                }
            }
            return s;
        }
        return "";
    }
}
