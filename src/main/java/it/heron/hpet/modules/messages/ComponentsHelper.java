package it.heron.hpet.modules.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

import java.util.LinkedList;
import java.util.List;

public class ComponentsHelper {

    public static Component simpleParse(String text) {
        if(text == null) {
            text = "null";
            Bukkit.getLogger().severe("Hyper Pets just prevented a NullPointerException, please make sure you included all required values into your configurations");
        }
        return MiniMessage.miniMessage().deserialize(text);
    }

    public static List<Component> listParse(List<String> text) {
        if(text == null || text.isEmpty()) {
            text = List.of("null");
            Bukkit.getLogger().severe("Hyper Pets just prevented a NullPointerException, please make sure you included all required values into your configurations");
        }
        List<Component> list = new LinkedList<>();
        for(String string : text) {
            list.add(simpleParse(string));
        }
        return list;
    }

}
