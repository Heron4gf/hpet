/*
 * This file is part of HPET - Packet Based Pet Plugin
 *
 * TOS (Terms of Service)
 * You are not allowed to decompile, or redestribuite part of this code if not authorized by the original author.
 * You are not allowed to claim this resource as yours.
 */


package it.heron.hpet.main;

import it.heron.hpet.database.Database;
import it.heron.hpet.database.databasetype.DatabaseType;
import it.heron.hpet.database.redisdatabase.RedisDatabase;
import it.heron.hpet.database.sqldatabases.MariaDB;
import it.heron.hpet.database.sqldatabases.MySQL;
import it.heron.hpet.database.sqldatabases.SQLite;
import it.heron.hpet.events.Events;
import it.heron.hpet.events.NewEvents;
import it.heron.hpet.animation.AnimationType;
import it.heron.hpet.api.events.HPETReloadPluginEvent;
import it.heron.hpet.combat.Deluxe;
import it.heron.hpet.commands.Commands;
import it.heron.hpet.itemsaddersupport.ItemsAdderListener;
import it.heron.hpet.legacyevents.LegacyEvents;
import it.heron.hpet.levels.LevelEvents;
import it.heron.hpet.messages.Messages;
import it.heron.hpet.metrics.Metrics;
import it.heron.hpet.pettypes.CosmeticType;
import it.heron.hpet.pettypes.PetType;
import it.heron.hpet.updater.AutoUpdater;
import it.heron.hpet.updater.UpdaterEventListener;
import it.heron.hpet.userpets.UserPet;
import it.heron.hpet.vanish.CMIVanish;
import it.heron.hpet.vanish.EssentialsVanish;
import it.heron.hpet.vanish.SuperVanish;
import it.heron.hpet.vanish.Vanish;
import it.heron.hpet.versionapi.PlayerVersion;
import lombok.Getter;
import lombok.Setter;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import it.heron.hpet.api.API;
import it.heron.hpet.groups.Group;
import it.heron.hpet.groups.HSlot;
import it.heron.hpet.packetutils.PacketUtils;
import it.heron.hpet.packetutils.versions.*;
import it.heron.hpet.placeholders.Placeholders;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class PetPlugin extends JavaPlugin {


    @Getter
    private static PetPlugin instance;

    @Getter
    private static API api = new API();

    @Getter
    private boolean rotate;

    @Getter
    private PacketUtils packetUtils = new Utils1_12();

    @Getter
    private int yawCalibration;

    @Getter
    private List<String> disabledWorlds = new ArrayList<>();

    @Getter
    private final HashMap<String, ItemStack> cachedItems = new HashMap<>();
    public void addToCache(String name, ItemStack item) {
        this.cachedItems.put(name, item);
    }

    @Getter
    private HashMap<UUID, Integer> openedPage = new HashMap<>();
    public void setOpenedPage(Player p, int n) {
        this.openedPage.put(p.getUniqueId(), n);
    }
    public void removeFromOpenedPage(Player p) {
        this.openedPage.remove(p.getUniqueId());
    }

    @Getter
    private CachedConfiguration cachedConfigurationInfo;

    private YamlConfiguration config;

    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    @Override
    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(new File(getDataFolder()+File.separator+"config.yml"));
        this.yawCalibration = getConfig().getInt("yawCalibration", 0);
        this.rotate = getConfig().getBoolean("rotateWithPlayer", false);
        this.nameFormat = getConfig().getString("nametags.format", "%name%");
        CosmeticType.reloadCosmeticsFiles();
    }


    @Getter @Setter
    private ArrayList<HSlot> petTypes = new ArrayList<>();
    public void parsePetTypes() {
        List<String> enabledPets = getConfig().getStringList("enabledPets");
        if(enabledPets.get(0).equals("*")) {
            enabledPets.clear();
            for(String s : this.petConfiguration.getKeys(false)) {
                enabledPets.add(s);
            }
        }



        for(String string : enabledPets) {

            try {
                if(string.startsWith("group:")) {
                    String gname = string.replaceFirst("group:", "");
                    List<String> configTypes = new LinkedList<>();

                    if(getConfig().contains("group."+gname+".pets")) configTypes = getConfig().getStringList("group."+gname+".pets");
                    if(getConfig().contains("group."+gname+".cosmetics")) configTypes.addAll(getConfig().getStringList("group."+gname+".cosmetics"));

                    PetType[] types = new PetType[configTypes.size()];
                    Bukkit.getLogger().info("Loading Pet group "+gname+" x"+configTypes.size());
                    for(int i = 0; i < configTypes.size(); i++) {
                        try {
                            types[i] = new PetType(configTypes.get(i));
                        } catch (RuntimeException ignored) {
                            types[i] = new CosmeticType(configTypes.get(i));
                        }
                    }
                    petTypes.add(new Group(gname, types));
                } else {
                    if(string.startsWith("cosmetic:")) {
                        Bukkit.getLogger().info("Loading cosmetic "+string);
                        petTypes.add(new CosmeticType(string));
                    } else {
                        Bukkit.getLogger().info("Loading pet "+string);
                        petTypes.add(new PetType(string));
                    }
                }
            } catch(Exception ignored) {
                Bukkit.getLogger().severe("Could not load pet/group "+string);
                ignored.printStackTrace();
            }

        }
        //NewEvents.a();
    }

    public static PetType getPetTypeByName(String name) {
        if(name == null) return null;
        for(HSlot slot : PetPlugin.getInstance().getPetTypes()) {
            if(slot instanceof PetType) {
                if(((PetType)slot).getName().equalsIgnoreCase(name)) {
                    return ((PetType)slot);
                }
            } else {
                for(PetType type : ((Group)slot).getType()) {
                    if(type.getName().equals(name)) {
                        return type;
                    }
                }
            }
        }
        return null;
    }

    public File getPetFile() {
        File file = new File(getDataFolder()+File.separator+"pets.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    @Getter
    private String nameFormat;

    @Getter @Setter
    private YamlConfiguration petConfiguration;

    @Getter
    private HeadDatabaseAPI headAPI;

    @Getter
    private boolean isUsingLegacyId = false;

    @Getter
    private boolean isUsingLegacySound = false;

    @Getter
    private Economy economy = null;

    @Getter
    private Vanish vanish = new Vanish();

    @Getter
    private PlayerVersion versionParser = new PlayerVersion();

    private boolean hook(String pluginName) {
        if(Bukkit.getPluginManager().getPlugin(pluginName) != null) {
            Bukkit.getLogger().info("Hooked with "+pluginName+" successfully!");
            return true;
        }
        return false;
    }

    public void clearCachedItems() {
        cachedItems.clear();
    }

    private boolean checkVersion(String version) {
        if(Bukkit.getServer().getVersion().contains(version)) {
            Bukkit.getLogger().info("Using "+version+" packets!");
            return true;
        }
        return false;
    }

    public static PacketUtils getPackUtils() {
        return instance.packetUtils;
    }

    @Getter
    private Database database;

    @Getter
    private Plugin[] addons = {null};

    @Getter
    private final boolean demo = false;


    @Getter
    private boolean PAPIhooked = false;

    private String lastUsedVersion = getDescription().getVersion();

    private Metrics metrics;

    @Override
    public void saveResource(String resource, boolean overwrite) {
        if(new File(getDataFolder()+File.separator+resource).exists()) return;
        super.saveResource(resource,overwrite);
    }

    @Override
    public void onEnable() {
        instance = this;
        saveResource("config.yml", demo);
        saveResource("pets.yml", demo);
        reloadConfig();
        convertConfig();
        this.cachedConfigurationInfo = new CachedConfiguration(this);

        this.petConfiguration = YamlConfiguration.loadConfiguration(getPetFile());
        //createGUIFile();

        String[] commands = {"hpet"};
        for(String s : commands) {
            getCommand(s).setExecutor(new Commands());
            Bukkit.getLogger().info("Registered /"+s+" command!");
        }
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        try {
            Bukkit.getPluginManager().registerEvents(new NewEvents(), this);
        } catch(Exception ignored)  {
            Bukkit.getLogger().info("TabComplete is not avaiable on this Minecraft version!");
        }

        switch (cachedConfigurationInfo.getDatabaseType()) {
            case SQLITE:
                this.database = new SQLite(this);
                break;
            case MYSQL:
                this.database = new MySQL(this);
                break;
            case MARIADB:
                this.database = new MariaDB(this);
                break;
            case REDIS:
                this.database = new RedisDatabase(this);
                break;
        }

        try {
            this.database.load();
            Bukkit.getLogger().info("Database loaded successfully!");
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while loading database");
            e.printStackTrace();
        }

        if(hook("PlaceholderAPI")) {
            new Placeholders().register();
            PAPIhooked = true;
        }
        if(hook("DeluxeCombat") && getConfig().getBoolean("deluxeCombatHook")) {
            Bukkit.getPluginManager().registerEvents(new Deluxe(), this);
        }
        if(hook("ItemsAdder")) {
            Bukkit.getPluginManager().registerEvents(new ItemsAdderListener(), this);
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                reload();
            },500);
        }

        String version = Bukkit.getServer().getVersion();
        //if(checkVersion("1.21.3") || checkVersion("1.21.4") || checkVersion("1.21.5") || checkVersion("1.22")) this.packetUtils = new Utils1_21_3();
        if(checkVersion("1.20.4") || checkVersion("1.20.5") || checkVersion("1.20.6") || checkVersion("1.21")) this.packetUtils = new Utils1_20_4();
        else if(checkVersion("1.19.3") || checkVersion("1.19.4") || checkVersion("1.20") || checkVersion("1.21")) this.packetUtils = new Utils1_19_3();
        else if(checkVersion("1.17") || checkVersion("1.18") || checkVersion("1.19")) this.packetUtils = new Utils1_17();
        else if(checkVersion("1.16")) this.packetUtils = new Utils1_16();
        else if(checkVersion("1.15")) this.packetUtils = new Utils1_15();
        else if(checkVersion("1.12")) this.packetUtils = new Utils1_12();

        if(checkVersion("1.8")) {
            this.packetUtils = new Utils1_8();
            saveResource("legacy_gui.yml", demo);
        } else {
            saveResource("gui.yml", demo);
        }
        for(String s : getConfig().getStringList("disabledWorlds")) {
            try {
                this.disabledWorlds.add(s);
            } catch(Exception ignored) {
                Bukkit.getLogger().warning(s+" world not found! Check disabledWorlds in config.yml!");
            }
        }
        for(int i = 12; i > 7; i--) {
            if(version.contains("1."+i)) {
                this.isUsingLegacyId = true;
                Bukkit.getLogger().info("Found legacy version: 1."+i);
                if(i == 8) {
                    this.isUsingLegacySound = true;
                    Bukkit.getPluginManager().registerEvents(new LegacyEvents(), this);
                }
            }
        }

        if(hook("HeadDatabase")) {
            this.headAPI = new HeadDatabaseAPI();
        }
        hook("MythicMobs");
        hook("HeadDB");
        if(hook("Vault")) {
            try {
                this.economy = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
            } catch(Exception ignored) {
                Bukkit.getLogger().warning("You are not using an Economy plugin, Vault is not hooked");
            }
        }

        if(getConfig().contains("vanish") && getConfig().getBoolean("vanish")) {
            if(hook("Essentials") || hook("EssentialsX")) {
                this.vanish = new EssentialsVanish();
            }
            if(hook("CMI")) {
                this.vanish = new CMIVanish();
            }
            if(hook("SuperVanish") || hook("PremiumVanish")) {
                this.vanish = new SuperVanish();
            }
        }



        AnimationType.setConst();


        if(getConfig().getBoolean("level.enable")) {
            Bukkit.getPluginManager().registerEvents(new LevelEvents(), this);
        }
        for(Player p : Bukkit.getOnlinePlayers()) {
            Utils.loadDatabasePet(p);
        }
        for(World world : Bukkit.getWorlds()) {
            for(Entity e : world.getEntities()) {
                if(e.getType() == EntityType.ARMOR_STAND && e.getName().equals("hpet")) e.remove();
            }
        }

        parsePetTypes();
        packetUtils.initDestroyListener();

        this.database.convertToNewerVersion(this.lastUsedVersion);

        Bukkit.getPluginManager().registerEvents(new UpdaterEventListener(), this);
        this.metrics = new Metrics(this, 14210);
        metrics.addCustomChart(new Metrics.SimplePie("plugin_version", () -> AutoUpdater.currentVersion()));

        /*Bukkit.getScheduler().scheduleSyncDelayedTask(this, () ->  {
            new HDEVCommandHandler(this);
        }, 300);*/

        new AddonsManager();
    }

    public void reload() {
        PetPlugin.getInstance().reloadConfig();
        Messages.rl();
        PetPlugin.getInstance().setPetConfiguration(YamlConfiguration.loadConfiguration(PetPlugin.getInstance().getPetFile()));
        PetPlugin.getInstance().setPetTypes(new ArrayList<>());
        PetPlugin.getInstance().parsePetTypes();
        PetPlugin.getInstance().clearCachedItems();
        AddonsManager.INSTANCE.reload();
        Bukkit.getPluginManager().callEvent(new HPETReloadPluginEvent());
        Bukkit.getLogger().info("Reloaded HPET!");
    }

    private void convertConfig() {
        if(getConfig().contains("mysql")) {
            DatabaseType databaseType = DatabaseType.SQLITE;

            String host = "localhost";
            String name = "hpet";
            int port = 3306;
            String user = "root";
            String password = "password";

            if(getConfig().getBoolean("mysql.enabled")) {
                databaseType = DatabaseType.MYSQL;
                if(getConfig().getBoolean("mysql.useMariaDb")) {
                    databaseType = DatabaseType.MARIADB;
                }

                host = getConfig().getString("mysql.address");
                port = getConfig().getInt("mysql.port");
                name = getConfig().getString("mysql.name");
                user = getConfig().getString("mysql.user");
                password = getConfig().getString("mysql.password");
            } else if(getConfig().getBoolean("redis")) {
                databaseType = DatabaseType.REDIS;

                host = getConfig().getString("redis.address");
                port = getConfig().getInt("redis.port");
                name = getConfig().getString("redis.name");
                user = getConfig().getString("redis.user");
                password = getConfig().getString("redis.password");
            }

            getConfig().set("database.type", databaseType.name());
            getConfig().set("database.name", name);
            getConfig().set("database.address", host);
            getConfig().set("database.port", port);
            getConfig().set("database.user", user);
            getConfig().set("database.password", password);


            getConfig().set("mysql", null);
            getConfig().set("redis", null);

            this.lastUsedVersion = "4.6";
        }
        try {
            getConfig().save(new File(getDataFolder()+File.separator+"config.yml"));
        } catch (IOException exception) {
            Bukkit.getLogger().warning("There was an error converting Config!");
            exception.printStackTrace();
        }
        reloadConfig();
        Bukkit.getLogger().warning("Config was converted to a newer version");
    }


    private File getFolder(String name) {
        return new File(getDataFolder().getParent()+File.separator+name);
    }


    @Getter @Setter
    private boolean cancellingListener = false;

    @Override
    public void onDisable() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            List<UserPet> userPets = PetPlugin.getApi().getUserPets(player);
            if(userPets != null && !userPets.isEmpty()) {
                Utils.savePets(player,userPets);
            } else {
                Utils.savePets(player,null);
            }
        }
        for(UserPet pet : PetPlugin.getPackUtils().getPets()) {
            if(pet.getOwner() != null) {
                pet.despawn();
            }
        }
        this.database.close();
    }
}
