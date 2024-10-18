package it.heron.hpet.updater;

import lombok.Data;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.*;

public class HDEVCommandHandler implements CommandExecutor, Listener {

    private static final String UPDATER_INFO_URL = "https://hdev.it/updater.info";
    private static final String UPDATER_INFO_PATH = "updater.info";

    private JavaPlugin javaPlugin;
    private Set<HDEVPlugin> plugins = new HashSet<>();

    private int installedPlugins() {
        int i = 0;
        for(HDEVPlugin hdevPlugin : plugins) {
            if(hdevPlugin.installed) {
                i++;
            }
        }
        return i;
    }


    public HDEVCommandHandler(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        javaPlugin.getCommand("hdev").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, javaPlugin);

        JSONObject offlineUpdaterInfo = readUpdaterInfoOffline();
        JSONObject onlineUpdaterInfo = getUpdaterInfoOnline();

        List<String> newlyExistingPlugins = new ArrayList<>();
        if(!onlineUpdaterInfo.isEmpty()) {
            for(String key : onlineUpdaterInfo.keySet()) {
                if(!offlineUpdaterInfo.has(key)) {
                    newlyExistingPlugins.add(key);
                }
            }
            offlineUpdaterInfo = onlineUpdaterInfo;
        }
        if(!offlineUpdaterInfo.isEmpty()) {
            loadExistingPlugins(offlineUpdaterInfo);
            saveUpdaterInfoOffline(offlineUpdaterInfo);

            for(HDEVPlugin hdevPlugin : plugins) {
                if(newlyExistingPlugins.contains(hdevPlugin.getName())) {
                    hdevPlugin.setJustnew(true);
                }
            }
        }
    }

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!player.hasPermission("hdev.admin")) return;
        for(HDEVPlugin hdevPlugin : plugins) {
            if(hdevPlugin.isJustnew() && !hdevPlugin.isInstalled()) {
                TextComponent linkMessage = new TextComponent();
                linkMessage.setText("§fA new HDEV plugin is available! §d"+hdevPlugin.getName()+" §d"+hdevPlugin.getDescription()+" §d" +ChatColor.UNDERLINE+"download");
                linkMessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, hdevPlugin.getDownloadURL()));
                player.spigot().sendMessage(linkMessage);
            }
        }
    }

    private void saveUpdaterInfoOffline(JSONObject updaterInfo) {
        File updaterInfoFile = new File(javaPlugin.getDataFolder() + File.separator + UPDATER_INFO_PATH);
        try (FileWriter fileWriter = new FileWriter(updaterInfoFile)) {
            fileWriter.write(updaterInfo.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadExistingPlugins(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            JSONObject pluginData = jsonObject.getJSONObject(key);

            String name = key;  // The key in the JSON is the plugin name
            String description = pluginData.getString("description");
            String downloadURL = pluginData.getString("download_link");

            List<String> addons = new ArrayList<>();
            if (pluginData.has("addons")) {
                for (Object addon : pluginData.getJSONArray("addons")) {
                    addons.add((String) addon);
                }
            }
            boolean installed = Bukkit.getPluginManager().getPlugin(name) != null;
            HDEVPlugin hdevPlugin = new HDEVPlugin(name, description, installed, downloadURL, addons);
            plugins.add(hdevPlugin);
        }
    }

    private JSONObject readUpdaterInfoOffline() {
        File updaterInfoFile = new File(javaPlugin.getDataFolder() + File.separator + UPDATER_INFO_PATH);
        if (!updaterInfoFile.exists()) {
            return new JSONObject();
        }

        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(updaterInfoFile))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                contentBuilder.append(currentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            return new JSONObject(contentBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getUpdaterInfoOnline() {
        try {
            URL url = new URL(UPDATER_INFO_URL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                conn.disconnect();
                return new JSONObject(content.toString());
            } else {
                Bukkit.getLogger().info("Failed to fetch latest version. HTTP response code: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("info")) {
                String plugin = args[1];
                HDEVPlugin selectedPlugin = null;
                for(HDEVPlugin hdevPlugin : plugins) {
                    if(hdevPlugin.getName().equalsIgnoreCase(plugin)) {
                        selectedPlugin = hdevPlugin;
                    }
                }

                if(selectedPlugin != null) {
                    sender.sendMessage("§f"+selectedPlugin.getName()+" §d"+selectedPlugin.getVersion());
                    sender.sendMessage("§f"+selectedPlugin.getDescription());
                    sender.sendMessage("§dUpdate URL: §f"+selectedPlugin.getDownloadURL());
                    if(!selectedPlugin.getAddons().isEmpty()) {
                        sender.sendMessage("§dAddons:");
                        for(String addon : selectedPlugin.getAddons()) {
                            try {
                                sender.sendMessage("  §d- §f"+addon+" §d"+Bukkit.getPluginManager().getPlugin(addon).getDescription().getVersion());
                            } catch (Exception ignored) {
                                sender.sendMessage("  §d- §7"+addon);
                            }
                        }
                    }
                    return true;
                } else {
                    for(HDEVPlugin hdevPlugin : plugins) {
                        for(String s : hdevPlugin.getAddons()) {
                            if(s.equalsIgnoreCase(plugin)) {
                                sender.sendMessage("§f"+s+" §dis an official addon of §f"+hdevPlugin.getName());
                                sender.sendMessage("§fVersion: §d"+Bukkit.getPluginManager().getPlugin(s).getDescription().getVersion());
                                return true;
                            }
                        }
                    }
                }
                sender.sendMessage("§fPlugin not found");
                return true;
            }
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("version")) {
                for(HDEVPlugin hdevPlugin : plugins) {
                    if (hdevPlugin.installed) {
                        sender.sendMessage("§f"+hdevPlugin.getName()+" §d"+hdevPlugin.getVersion());
                        if(!hdevPlugin.getAddons().isEmpty()) {
                            for(String addon : hdevPlugin.getAddons()) {
                                try {
                                    sender.sendMessage("  §d- §f"+addon+" §d"+Bukkit.getPluginManager().getPlugin(addon).getDescription().getVersion());
                                } catch (Exception ignored) {
                                    sender.sendMessage("  §d- §7"+addon);
                                }
                            }
                        }
                    }
                }
                return true;
            }
            if(args[0].equalsIgnoreCase("plugins")) {
                sender.sendMessage("§dHDEV Plugins §f("+installedPlugins()+"):");
                for(HDEVPlugin hdevPlugin : plugins) {
                    if(hdevPlugin.installed) {
                        TextComponent commandMessage = new TextComponent();
                        String addons = "";
                        if(!hdevPlugin.getAddons().isEmpty()) {
                            for(String addon : hdevPlugin.getAddons()) {
                                if(Bukkit.getPluginManager().getPlugin(addon) != null) {
                                    addon = ChatColor.LIGHT_PURPLE+addon;
                                }
                                addons = addons+addon+ChatColor.WHITE+", "+ChatColor.GRAY;
                            }
                            addons = addons.substring(0, addons.length()-4);

                            commandMessage.setText(ChatColor.LIGHT_PURPLE + hdevPlugin.getName() + ChatColor.WHITE+"("+ChatColor.GRAY+addons+")");
                        } else {
                            commandMessage.setText(ChatColor.LIGHT_PURPLE + hdevPlugin.getName());
                        }
                        commandMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hdev info "+hdevPlugin.getName()));
                        sender.spigot().sendMessage(commandMessage);
                    } else {
                        TextComponent linkMessage = new TextComponent();
                        linkMessage.setText(ChatColor.GRAY +""+ChatColor.UNDERLINE+ hdevPlugin.getName()+ChatColor.GRAY+" - "+hdevPlugin.getDescription());
                        linkMessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, hdevPlugin.getDownloadURL()));
                        sender.spigot().sendMessage(linkMessage);
                    }
                }
                return true;
            }
        }


        sender.sendMessage("§d§lHDEV §f- Your problem is our software solution");
        sender.sendMessage("§d/hdev plugins §f- show the HDEV plugins");
        sender.sendMessage("§d/hdev version §f- check the HDEV plugins version");
        sender.sendMessage("§d/hdev info <plugin> §f- get more info about a HDEV plugin");
        return true;
    }

    private @Data class HDEVPlugin {
        private String name;
        private String description;
        private boolean installed;
        private String downloadURL;
        private boolean justnew = false;
        private List<String> addons = new ArrayList<>();

        public HDEVPlugin(String name, String description, boolean installed, String downloadURL, List<String> addons) {
            this.name = name;
            this.description = description;
            this.installed = installed;
            this.downloadURL = downloadURL;
            this.addons.addAll(addons);
        }

        public String getVersion() {
            if(!installed) return null;
            return Bukkit.getPluginManager().getPlugin(name).getDescription().getVersion();
        }

    }
}
