package it.heron.hpet;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import it.heron.hpet.database.Database;
import it.heron.hpet.database.PetDatabase;
import it.heron.hpet.userpets.UnspawnedUserPet;
import it.heron.hpet.userpets.UserPet;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.mojang.authlib.GameProfile;
import org.bukkit.scheduler.BukkitRunnable;

public class Utils {

    public static ItemStack head(String skinName) {
        ItemStack stack = Pet.getInstance().getCachedItems().get(skinName);
        if(stack != null) {
            return stack;
        }
        if(Pet.getInstance().isUsingLegacyId()) {
            stack = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short)3);
        } else {
            stack = new ItemStack(Material.PLAYER_HEAD);
        }
        if(skinName.length() > 16) {
            setHeadTexture(stack, skinName);
        } else {
            if(skinName.startsWith("HDB:")) {
                return Pet.getInstance().getHeadAPI().getItemHead(skinName.replace("HDB:", ""));
            }
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            meta.setOwner(skinName);
            stack.setItemMeta(meta);
        }
        Pet.getInstance().addToCache(skinName, stack);
        return stack;
    }
    public static GameProfile createProfileWithTexture(String texture){

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);

        PropertyMap propertyMap = gameProfile.getProperties();
        propertyMap.put("textures", new Property("textures", texture));

        return gameProfile;

    }

    public static void setHeadTexture(ItemStack itemStack, String texture){
        if(itemStack != null){
            ItemMeta itemMeta = itemStack.getItemMeta();
            try {
                Field field = itemMeta.getClass().getDeclaredField("profile");
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                try {
                    field.set(itemMeta, createProfileWithTexture(texture));
                } finally {
                    if (!field.isAccessible()) {
                        field.setAccessible(false);
                    }
                }
            }catch(Exception ex){
                Bukkit.getConsoleSender().sendMessage("Error!");
            }
            itemStack.setItemMeta(itemMeta);
        }
    }

    public static ItemStack getCustomItem(String skin) {
        if(Pet.getInstance().getCachedItems().containsKey(skin)) {
            return Pet.getInstance().getCachedItems().get(skin);
        }
        if(!skin.contains(":")) {
            return head(skin);
        }

        String[] s = skin.split(":");
        if(s[0].equals("MOB")) {
            ItemStack stack;
            if(Pet.getInstance().isUsingLegacyId()) {
                stack = new ItemStack(Material.valueOf("MONSTER_EGG"));
            } else {
                stack = new ItemStack(Material.valueOf(s[1]+"_SPAWN_EGG"));
            }
            //Pet.getInstance().addToCache(skin, stack);
            return stack;
        }
        Material material;
        int customModelData;
        try {
            material = Material.valueOf(s[0]);
            customModelData = Integer.parseInt(s[1]);
        } catch(Exception e) {
            return head(skin);
        }
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        try {
            meta.setCustomModelData(customModelData);
        } catch(Exception ignored) {}
        stack.setItemMeta(meta);
        Pet.getInstance().addToCache(skin, stack);
        return stack;
    }

    public static TextComponent text(String text, ClickEvent.Action action, String value) {
        TextComponent t = new TextComponent(text);
        t.setClickEvent(new ClickEvent(action, value));
        return t;
    }

    public static int getRandomId() {
        double did = Math.random()*Integer.MAX_VALUE;
        return (int)did;
    }

    public static ItemStack editStack(ItemStack stack, String name, List<String> lore) {
        if(stack.getType() == Material.AIR) return stack;
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(lore);
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
        return stack;
    }

    public static String[] fromList(List<String> list) {
        String[] ret = new String[list.size()];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }

    public static void loadVisiblePets(Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(UserPet opet: Pet.getInstance().getPacketUtils().getPets()) {
                    if(opet != null && opet.getOwner() != null && !opet.getOwner().equals(p)) {
                        if(Bukkit.getPlayer(opet.getOwner()).getWorld().getUID().equals(p.getWorld().getUID()) && Bukkit.getPlayer(opet.getOwner()).getLocation().distance(p.getLocation()) < 50) {
                            opet.update();
                        }
                    }
                }
            }
        }.runTaskLater(Pet.getInstance(), 20);
    }

    public static void runAsync(Runnable runnable) {
        CompletableFuture.runAsync(runnable);
    }

    public static void loadDatabasePet(Player p) {
        for(UnspawnedUserPet unspawnedUserPet : Pet.getInstance().getDatabase().getUnspawnedPets(p)) {
            if(unspawnedUserPet != null) {
                unspawnedUserPet.toUserPet();
            }
        }
    }

    public static void makeSureThisArmorstandIsNotRealPlease(int id, World world) {
        for(Entity e : world.getEntities()) {
            if(e.getEntityId() == id) {
                e.remove();
                Pet.getPackUtils().executePacket(Pet.getPackUtils().destroyEntity(id), world);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Pet.getPackUtils().executePacket(Pet.getPackUtils().destroyEntity(id), world);
                    }
                }.runTaskLater(Pet.getInstance(), 5);
                return;
            }
        }
    }

    public static ItemStack getGUIStack(String path) {
        if(Pet.getInstance().getCachedItems().containsKey(path)) return Pet.getInstance().getCachedItems().get(path);
        path = "gui."+path+".";
        YamlConfiguration data;
        if(Pet.getInstance().isUsingLegacyId()) {
            data = YamlConfiguration.loadConfiguration(new File(Pet.getInstance().getDataFolder()+File.separator+"legacy_gui.yml"));
        } else {
            data = YamlConfiguration.loadConfiguration(new File(Pet.getInstance().getDataFolder()+File.separator+"gui.yml"));
        }
        Pet.getInstance().addToCache(path, createStack(Material.valueOf(data.getString(path+"material")), color(data.getString(path+"name")), color(data.getStringList(path+"desc"))));
        return getGUIStack(path);
    }

    public static void savePets(Player p, List<UserPet> pets) {
        Pet.getInstance().getDatabase().wipePets(p);

        for(UserPet pet : pets) {
            Pet.getInstance().getDatabase().savePet(pet);
        }
    }

    public static List<String> color(List<String> strings) {
        for(int i = 0; i < strings.size(); i++) {
            strings.set(i, color(strings.get(i)));
        }
        return strings;
    }
    public static String color(String string) {
        if(string == null) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static ItemStack createStack(Material material, String name, List<String> lore) {
        return editStack(new ItemStack(material), name, lore);
    }

    public static EnumWrappers.ItemSlot fromEquipSlot(EquipmentSlot slot) {
        switch(slot) {
            case HAND:
                return EnumWrappers.ItemSlot.MAINHAND;
            case HEAD:
                return EnumWrappers.ItemSlot.HEAD;
            default:
                return null;
        }
    }
}
