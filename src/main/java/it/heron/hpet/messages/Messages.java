package it.heron.hpet.messages;

import it.heron.hpet.Pet;
import it.heron.hpet.Utils;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Messages {

    private static final Messages manager = new Messages();
    public static String getMessage(String path) {
        return manager.gmessage(path);
    }
    public static List<String> getList(String path) {
        return manager.glist(path);
    }

    public void clear() {
        this.messages.clear();
    }
    public void reload() {
        clear();
        this.messagesFile = YamlConfiguration.loadConfiguration(getFile());
    }

    public static void rl() {
        manager.reload();
    }
    private YamlConfiguration messagesFile;

    public File getFile() {
        return new File(Pet.getInstance().getDataFolder()+File.separator+"messages.yml");
    }

    public Messages() {
        if(!new File(Pet.getInstance().getDataFolder()+File.separator+"messages.yml").exists()) Pet.getInstance().saveResource("messages.yml", false);
        this.messagesFile = YamlConfiguration.loadConfiguration(getFile());
    }

    private HashMap<String, String> messages = new HashMap<>();

    public String gmessage(String path) {

        if(!messages.containsKey(path)) {
            String string = this.messagesFile.getString("messages."+path);
            if(string == null) {
                Pet.getInstance().getConfig().set(path, path);
                try {
                    this.messagesFile.save(getFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                string = path;
            }
            messages.put(path, Utils.color(string));
        }
        return messages.get(path);
    }
    public List<String> glist(String path) {
        return Utils.color(this.messagesFile.getStringList("messages."+path));
    }
}