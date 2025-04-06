/*
 * This file is part of HPET - Packet Based Pet Plugin
 *
 * TOS (Terms of Service)
 * You are not allowed to decompile, or redestribuite part of this code if not authorized by the original author.
 * You are not allowed to claim this resource as yours.
 */


package it.heron.hpet.main;

import it.heron.hpet.api.events.HPETReloadPluginEvent;

import it.heron.hpet.modules.messages.MessagesHandler;
import it.heron.hpet.modules.ModulesHandler;
import it.heron.hpet.modules.pets.PetTypesHandler;
import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;
import it.heron.hpet.modules.pets.userpets.fakeentities.armorstandmetadatahandlers.ArmorStandMetadataHandler;
import it.heron.hpet.modules.pets.PetsHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import it.heron.hpet.api.PetAPI;

import java.io.File;
import java.util.*;

public final class PetPlugin extends JavaPlugin {

    @Getter
    private static PetPlugin instance;

    @Getter
    private static PetAPI api = new PetAPI();

    @Getter
    private ArmorStandMetadataHandler armorStandMetadataHandler;

    @Getter
    private List<String> disabledWorlds = new ArrayList<>();

    @Getter
    private final PetTypesHandler petTypesHandler = new PetTypesHandler();

    private YamlConfiguration config;

    @Getter
    private final ModulesHandler modulesHandler = new ModulesHandler(this);

    @Override
    public FileConfiguration getConfig() {
        return config;
    }


    @Override
    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(new File(getDataFolder()+File.separator+"config.yml"));
    }



    @Override
    public void saveResource(String resource, boolean overwrite) {
        if(new File(getDataFolder()+File.separator+resource).exists()) return;
        super.saveResource(resource, overwrite);
    }

    @Override
    public void onEnable() {
        load();
    }

    @Override
    public void onDisable() {
        unload();
    }

    public void reload() {
        PetPlugin.getInstance().reloadConfig();
        unload();
        load();
        Bukkit.getPluginManager().callEvent(new HPETReloadPluginEvent());
        Bukkit.getLogger().info("Reloaded HPET!");
    }

    private void unload() {
        for(UserPet userPet : PetPlugin.getApi().spawnedPets()) {
            PetPlugin.getApi().removePet(userPet);
        }
        this.modulesHandler.unloadModules();
    }

    private void load() {
        instance = this;
        saveResource("config.yml", false);
        saveResource("pets.yml", false);
        reloadConfig();

        for(Player p : Bukkit.getOnlinePlayers()) {
            Utils.loadDatabasePet(p);
        }

        this.modulesHandler.loadModules();
    }


}
