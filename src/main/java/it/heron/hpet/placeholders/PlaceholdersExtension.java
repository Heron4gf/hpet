package it.heron.hpet.placeholders;

import it.heron.hpet.levels.LevelEvents;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.pets.userpets.old.HeadUserPet;

public class PlaceholdersExtension extends PlaceholderExpansion {

    //private Pet plugin;

    public PlaceholdersExtension() { }

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
    public String onRequest(OfflinePlayer p, String identifier) {
        HeadUserPet pet;
        if(p.isOnline()) {
            pet = PetPlugin.getApi().getUserPet(p.getPlayer());
        } else {
            return "";
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
