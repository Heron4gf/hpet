package it.heron.hpet.messages;

import io.lumine.mythic.bukkit.utils.adventure.text.Component;
import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.main.Utils;

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

    public static Component getComponent(String path) {
        return manager.cmessage(path);
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
        return new File(PetPlugin.getInstance().getDataFolder()+File.separator+"messages.yml");
    }

    public Messages() {
        if(!new File(PetPlugin.getInstance().getDataFolder()+File.separator+"messages.yml").exists()) PetPlugin.getInstance().saveResource("messages.yml", false);
        this.messagesFile = YamlConfiguration.loadConfiguration(getFile());
    }

    private HashMap<String, String> messages = new HashMap<>();
    private HashMap<String, Component> components = new HashMap<>();

    public Component cmessage(String path) {

        if(!components.containsKey(path)) {
            String string = this.messagesFile.getString("messages."+path);
            if(string == null) {
                PetPlugin.getInstance().getConfig().set(path, path);
                try {
                    this.messagesFile.save(getFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                string = path;
            }
            components.put(path, Utils.mini_color(string));
        }
        return components.get(path);
    }

    public String gmessage(String path) {

        if(!messages.containsKey(path)) {
            String string = this.messagesFile.getString("messages."+path);
            if(string == null) {
                PetPlugin.getInstance().getConfig().set(path, path);
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