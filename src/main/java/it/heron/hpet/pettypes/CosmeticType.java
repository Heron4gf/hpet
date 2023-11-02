package it.heron.hpet.pettypes;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import it.heron.hpet.Pet;
import it.heron.hpet.Utils;
import it.heron.hpet.abilities.AbilityExecutor;
import it.heron.hpet.animation.AnimationType;
import lombok.Getter;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CosmeticType extends PetType {

    private EquipmentSlot equipmentSlot = EquipmentSlot.HEAD;

    @Getter
    private boolean wearable = false;

    @Getter
    private Color color = null;

    public static void reloadCosmeticsFiles() {
        try {
            File cosmetics_folder = new File(Pet.getInstance().getDataFolder(),"cosmetics");
            if(!cosmetics_folder.exists()) cosmetics_folder.mkdir();

            cosmeticsFiles.clear();
            for(File file : cosmetics_folder.listFiles()) {
                try {
                    cosmeticsFiles.add(YamlConfiguration.loadConfiguration(file));
                } catch (Exception ignored) {
                    Bukkit.getLogger().warning("An error occurred while using "+file.getName()+" as cosmetics file");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("There was an error while reading into cosmetics folder");
        }
    }

    @Getter
    private final static Set<YamlConfiguration> cosmeticsFiles = new HashSet<>();

    @Override
    public ItemStack getIcon(Player player) {
        return Utils.colorArmor(super.getIcon(player),color);
    }

    public CosmeticType(String name) {
        YamlConfiguration data = null;
        for(YamlConfiguration yaml : cosmeticsFiles) {
            if(yaml.contains(name)) {
                data = yaml;
                break;
            }
            if(yaml.contains("import_itemsadder")) {
                String item = yaml.getString("import_itemsadder")+":"+name;
                if(CustomStack.getInstance(item) != null) {
                    data = yaml;
                    break;
                }
            }
        }

        if(data.contains("import_itemsadder") && Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            String item = data.getString("import_itemsadder")+":"+name;
            this.setSkins(new String[]{"ITEMSADDER:"+item});

            CustomStack customStack = CustomStack.getInstance(item);
            ItemStack itemStack = customStack.getItemStack();
            this.setDisplayName(itemStack.hasItemMeta() ? Utils.color("&e"+name) : itemStack.getItemMeta().getDisplayName());
            this.setDescription(itemStack.hasItemMeta() ? Arrays.asList("") : itemStack.getItemMeta().getLore());

            boolean hat = false;
            try {
                hat = customStack.getConfig().getBoolean("items."+customStack.getId()+".behaviours.hat");
            } catch (Exception ignored) {}
            if(!hat) {
                equipmentSlot = EquipmentSlot.OFF_HAND;
            }
            try {
                color = ((LeatherArmorMeta)itemStack.getItemMeta()).getColor();
            } catch (Exception ignored) {}
        }

        if(data.contains(name+".wear")) {
            equipmentSlot = EquipmentSlot.valueOf(data.getString(name+".wear"));
        }
        setGroup(equipmentSlot.name());

        if(data.contains(name+".color")) {
            String hex = data.getString(name+".color");
            TextColor color = TextColor.fromHexString(hex);
            this.color = Color.fromRGB(color.red(),color.green(),color.blue());
        }

        setName(name);
        String item = data.getString(name+".item");
        if(item != null) setSkins(Utils.fromList(Arrays.asList(item)));

        List<EquipmentSlot> pretend_slots = Arrays.asList(EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET);
        setAnimation(AnimationType.NONE);
        if(pretend_slots.contains(equipmentSlot)) {
            setNamey(data.getDouble(name+".y",1.8));
            setYaw(data.getInt(name+".yaw",Pet.instance.getYawCalibration())+180);
            setCustomModelData(true);
            wearable = true;
            setDistance(0);
        } else {
            setVisible(false);
            setAbilities(Arrays.asList(new AbilityExecutor("FAKE_ARMOR:"+equipmentSlot.name()+":"+getSkins()[0]+":1s")));
        }

        if(data.contains(name+".displayname")) setDisplayName(Utils.color(data.getString(name+".displayname")));
        if(data.contains(name+".description")) setDescription(Utils.color(data.getStringList(name+".description")));

        if(data.contains(name+".price")) {
            setPrice(data.getDouble(name + ".price"));
        }
    }
}
