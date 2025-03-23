package it.heron.hpet.main;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.bukkit.utils.adventure.text.Component;
import io.lumine.mythic.bukkit.utils.adventure.text.minimessage.MiniMessage;
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
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.mojang.authlib.GameProfile;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;

public class Utils {

    public static ItemStack head(String skinName) {
        ItemStack stack = PetPlugin.getInstance().getCachedItems().get(skinName);
        if(stack != null) {
            return stack;
        }
        if(PetPlugin.getInstance().isUsingLegacyId()) {
            stack = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short)3);
        } else {
            stack = new ItemStack(Material.PLAYER_HEAD);
        }
        if(skinName.length() > 16) {
            setHeadTexture(stack, skinName);
        } else {
            if(skinName.startsWith("HDB:")) {
                return PetPlugin.getInstance().getHeadAPI().getItemHead(skinName.replace("HDB:", ""));
            }
            SkullMeta meta = (SkullMeta) stack.getItemMeta();

            if(PetPlugin.getPackUtils().isLegacy()) {
                meta.setOwner(skinName);
            } else {
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(skinName));
            }

            stack.setItemMeta(meta);
        }
        PetPlugin.getInstance().addToCache(skinName, stack);
        return stack;
    }

    public static GameProfile createProfileWithTexture(String texture){

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "hpet_gameprofile");

        PropertyMap propertyMap = gameProfile.getProperties();
        propertyMap.put("textures", new Property("textures", texture));

        return gameProfile;
    }

    public static URL getUrlFromBase64(String base64) throws MalformedURLException {
        String decoded = new String(Base64.getDecoder().decode(base64));
        // We simply remove the "beginning" and "ending" part of the JSON, so we're left with only the URL. You could use a proper
        // JSON parser for this, but that's not worth it. The String will always start exactly with this stuff anyway
        return new URL(decoded.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(), decoded.length() - "\"}}}".length()));
    }

    public static boolean isBase64(String text) {
        return text.length() > 64;
    }

    public static PlayerProfile createConsistentProfileWithTexture(String texture) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), "hello");
        PlayerTextures textures = profile.getTextures();
        try {
            URL url = null;
            if(isBase64(texture)) {
                url = getUrlFromBase64(texture);
            } else {
                url = new URL(texture);
            }

            textures.setSkin(url);
            profile.setTextures(textures);
        } catch (MalformedURLException ignored) {}
        return profile;
    }



    public static void setHeadTexture(ItemStack itemStack, String texture){
        if(itemStack != null){
            ItemMeta itemMeta = itemStack.getItemMeta();


            boolean consistent = PetPlugin.getInstance().getConfig().getBoolean("fix.consistent_heads");
            if(consistent) {
                SkullMeta meta = (SkullMeta)itemMeta;
                meta.setOwnerProfile(createConsistentProfileWithTexture(texture));
            } else {
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
                    Bukkit.getConsoleSender().sendMessage("Error while generating a textured head!");
                }
            }
            itemStack.setItemMeta(itemMeta);
        }
    }

    public static ItemStack getCustomItem(String skin) {
        if(PetPlugin.getInstance().getCachedItems().containsKey(skin)) {
            return PetPlugin.getInstance().getCachedItems().get(skin);
        }
        if(!skin.contains(":")) {
            return head(skin);
        }

        String[] s = skin.split(":");
        if(s[0].equals("MOB")) {
            ItemStack stack;
            if(PetPlugin.getInstance().isUsingLegacyId()) {
                stack = new ItemStack(Material.valueOf("MONSTER_EGG"));
            } else {
                stack = new ItemStack(Material.valueOf(s[1]+"_SPAWN_EGG"));
            }
            //Pet.getInstance().addToCache(skin, stack);
            return stack;
        }
        if(s[0].equals("ITEMSADDER")) {
            return CustomStack.getInstance(skin.replace("ITEMSADDER:", "")).getItemStack();
        }
        Material material;
        int customModelData;
        try {
            material = Material.valueOf(s[0]);
            customModelData = Integer.parseInt(skin.replace(s[0]+":", ""));
        } catch(Exception e) {
            return head(skin);
        }
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        try {
            meta.setCustomModelData(customModelData);
        } catch(Exception ignored) {}
        stack.setItemMeta(meta);
        PetPlugin.getInstance().addToCache(skin, stack);
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
                for(UserPet opet: PetPlugin.getInstance().getPacketUtils().getPets()) {
                    if(opet != null && opet.getOwner() != null && !opet.getOwner().equals(p)) {
                        if(Bukkit.getPlayer(opet.getOwner()).getWorld().getUID().equals(p.getWorld().getUID()) && Bukkit.getPlayer(opet.getOwner()).getLocation().distance(p.getLocation()) < 50) {
                            opet.update();
                        }
                    }
                }
            }
        }.runTaskLater(PetPlugin.getInstance(), 20);
    }

    public static void runAsync(Runnable runnable) {
        CompletableFuture.runAsync(runnable);
    }

    public static void loadDatabasePet(Player p) {
        for(UnspawnedUserPet unspawnedUserPet : PetPlugin.getInstance().getDatabase().offlineUserPets(p)) {
            if(unspawnedUserPet != null) {
                unspawnedUserPet.toUserPet();
            }
        }
    }

    public static void makeSureThisArmorstandIsNotRealPlease(int id, World world) {
        for(Entity e : world.getEntities()) {
            if(e.getEntityId() == id) {
                e.remove();
                PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().destroyEntity(id), world);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        PetPlugin.getPackUtils().executePacket(PetPlugin.getPackUtils().destroyEntity(id), world);
                    }
                }.runTaskLater(PetPlugin.getInstance(), 5);
                return;
            }
        }
    }

    public static ItemStack getGUIStack(String path) {
        if(PetPlugin.getInstance().getCachedItems().containsKey(path)) return PetPlugin.getInstance().getCachedItems().get(path);
        path = "gui."+path+".";
        YamlConfiguration data;
        if(PetPlugin.getInstance().isUsingLegacyId()) {
            data = YamlConfiguration.loadConfiguration(new File(PetPlugin.getInstance().getDataFolder()+File.separator+"legacy_gui.yml"));
        } else {
            data = YamlConfiguration.loadConfiguration(new File(PetPlugin.getInstance().getDataFolder()+File.separator+"gui.yml"));
        }
        PetPlugin.getInstance().addToCache(path, createStack(Material.valueOf(data.getString(path+"material")), color(data.getString(path+"name")), color(data.getStringList(path+"desc"))));
        return getGUIStack(path);
    }

    public static void savePets(Player p, List<UserPet> pets) {
        PetPlugin.getInstance().getDatabase().wipeLastPets(p);

        if(pets != null) {
            for(UserPet pet : pets) {
                PetPlugin.getInstance().getDatabase().savePet(pet);
            }
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

    public static Component mini_color(String string) {
        if(string == null) {
            return null;
        }
        return MiniMessage.miniMessage().deserialize(string);
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

    public static ItemStack colorArmor(ItemStack stack, Color color) {
        stack = stack.clone();
        if(!stack.getType().name().startsWith("LEATHER_")) {
            return stack;
        }
        LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
        meta.setColor(color);
        stack.setItemMeta(meta);
        return stack;
    }
}
