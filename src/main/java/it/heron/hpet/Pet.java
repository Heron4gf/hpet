/*
 * This file is part of HPET - Packet Based Pet Plugin
 *
 * TOS (Terms of Service)
 * You are not allowed to decompile, or redestribuite part of this code if not authorized by the original author.
 * You are not allowed to claim this resource as yours.
 */


package it.heron.hpet;

import it.heron.hpet.animation.AnimationType;
import it.heron.hpet.combat.Deluxe;
import it.heron.hpet.database.Database;
import it.heron.hpet.legacyevents.LegacyEvents;
import it.heron.hpet.levels.LevelEvents;
import it.heron.hpet.pettypes.PetType;
import it.heron.hpet.userpets.UserPet;
import it.heron.hpet.vanish.CMIVanish;
import it.heron.hpet.vanish.EssentialsVanish;
import it.heron.hpet.vanish.SuperVanish;
import it.heron.hpet.vanish.Vanish;
import it.heron.hpet.versionapi.PlayerVersion;
import it.heron.hpet.versionapi.ViaVer;
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
import it.heron.hpet.database.MySQL;
import it.heron.hpet.database.SQLite;
import it.heron.hpet.groups.Group;
import it.heron.hpet.groups.HSlot;
import it.heron.hpet.packetutils.PacketUtils;
import it.heron.hpet.packetutils.versions.*;
import it.heron.hpet.placeholders.Placeholders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Pet extends JavaPlugin {

    @Getter
    public static Pet instance;

    @Getter
    private static API api = new API();

    @Getter
    private boolean rotate;

    @Getter
    private PacketUtils packetUtils = new Utils1_12();

    @Getter
    private int yawCalibration;

    @Getter
    private List<World> disabledWorlds = new ArrayList<>();

    @Getter
    private HashMap<String, ItemStack> cachedItems = new HashMap<>();
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
    }


    @Getter @Setter
    private ArrayList<HSlot> petTypes = new ArrayList<>();
    public void parsePetTypes() {
        List<String> enabledPets = getConfig().getStringList("enabledPets");
        if(enabledPets.get(0).equals("*")) {
            enabledPets = new ArrayList<>();
            for(String s : this.petConfiguration.getKeys(false)) {
                enabledPets.add(s);
            }
        }



        for(String string : enabledPets) {

            try {
                if(string.startsWith("group:")) {
                    String gname = string.replaceFirst("group:", "");
                    List<String> configTypes = getConfig().getStringList("group."+gname+".pets");
                    PetType[] types = new PetType[configTypes.size()];
                    Bukkit.getLogger().info("Loading Pet group "+gname+" x"+configTypes.size());
                    for(int i = 0; i < configTypes.size(); i++) {
                        types[i] = new PetType(configTypes.get(i));
                    }
                    petTypes.add(new Group(gname, types));
                } else {
                    Bukkit.getLogger().info("Loading pet "+string);
                    petTypes.add(new PetType(string));
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
        for(HSlot slot : Pet.getInstance().getPetTypes()) {
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

    @Override
    public void saveResource(String resource, boolean overwrite) {
        if(new File(getDataFolder()+File.separator+resource).exists()) return;
        super.saveResource(resource,overwrite);
    }

    @Override
    public void onEnable() {

        File oldFolder = getFolder("Pet");
        if(oldFolder.exists()) {
            File folder = getFolder("HPET");
            if(!folder.exists()) {
                if(oldFolder.renameTo(folder)) getFolder("Pet").delete();
            }
        }

        saveResource("config.yml", demo);
        saveResource("pets.yml", demo);
        reloadConfig();

        this.petConfiguration = YamlConfiguration.loadConfiguration(getPetFile());
        //createGUIFile();

        instance = this;

        String[] commands = {"pet", "hpet", "pets"};
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

        if(getConfig().getBoolean("mysql.enabled")) {
            this.database = new MySQL(this);
        } else {
            this.database = new SQLite(this);
        }
        this.database.load();
        Bukkit.getLogger().info("Database loaded successfully!");

        if(hook("PlaceholderAPI")) {
            new Placeholders().register();
        }
        if(hook("DeluxeCombat") && getConfig().getBoolean("deluxeCombatHook")) {
            Bukkit.getPluginManager().registerEvents(new Deluxe(), this);
        }

        String version = Bukkit.getServer().getVersion();

        if(checkVersion("1.17") || checkVersion("1.18") || checkVersion("1.19")) this.packetUtils = new Utils1_17();
        if(checkVersion("1.16")) this.packetUtils = new Utils1_16();
        if(checkVersion("1.15")) this.packetUtils = new Utils1_15();
        if(checkVersion("1.12")) {
            this.packetUtils = new Utils1_12();
            //Utils1_12.initDestroyListener();
        }
        if(checkVersion("1.8")) {
            this.packetUtils = new Utils1_8();
            saveResource("legacy_gui.yml", demo);
        } else {
            saveResource("gui.yml", demo);
        }

        for(String s : getConfig().getStringList("disabledWorlds")) {
            this.disabledWorlds.add(Bukkit.getWorld(s));
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
        if(hook("MythicMobs")) {
            //MythicUserPet.initDestroyListener();
        }
        if(hook("Vault")) {
            try {
                this.economy = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
            } catch(Exception ignored) {
                System.out.printf("Pet: You are not using an Economy plugin, Vault is not hooked");
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

        if(getConfig().getBoolean("useLevelEvents")) {
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

    }

    private File getFolder(String name) {
        return new File(getDataFolder().getParent()+File.separator+name);
    }

    private final Metrics metrics = new Metrics(this, 14210);

    @Getter @Setter
    private boolean cancellingListener = false;

    @Override
    public void onDisable() {
        for(UserPet pet : Pet.getInstance().getPacketUtils().getPets().values()) {
                Utils.savePet(pet.getOwner(), pet);
                if(pet != null) pet.despawn();
        }
        /*if(getConfig().getBoolean("filenameversion")) {
            this.getFile().renameTo(new File(this.getFile().getPath().replace("Pet.jar", "HPET-"+getDescription().getVersion()+".jar")));
        }*/
        /*for(Plugin plugin : addons) {
            try {
                plugin.getPluginLoader().disablePlugin(plugin);
            } catch(Exception ignored) {}
        }*/
    }
}
