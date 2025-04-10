package it.heron.hpet.modules.pets.pettypes;

import it.heron.hpet.main.guis.GUI;
import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.main.Utils;
import it.heron.hpet.abilities.AbilityExecutor;
import it.heron.hpet.levels.LType;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import it.heron.hpet.animation.AnimationType;
import it.heron.hpet.groups.HSlot;

import java.util.ArrayList;
import java.util.List;

public @Data
class OldPetType extends HSlot {

    private int yaw = 0;

    //private String displayName;
    private boolean customModelData = false;
    private boolean follow = false;
    private String[] skins = null;
    private List<String> description = null;
    //private Particle recommended;
    private LType ltype = LType.NONE;
    private String group = "default";

    private double distance = 1;
    private boolean visible = true;
    private double namey = 1;

    private boolean balloon = false;
    private double ballon_height = 0;

    private Double price = null;

    private AnimationType animation = AnimationType.GLIDE;
    private EntityType entityType = EntityType.ARMOR_STAND;
    private String mythicMob = null;

    private List<AbilityExecutor> abilities = new ArrayList<>();

    public OldPetType() {
    }

    private String modelEngine = null;
    public boolean isMythicMob() {return mythicMob != null;}
    public boolean isModelEngine() {
        return modelEngine != null;
    }
    public boolean isMob() {return entityType != EntityType.ARMOR_STAND;}

    public OldPetType(String name) {
        YamlConfiguration data = PetPlugin.getInstance().getPetConfiguration();

        if(!data.contains(name)) throw new RuntimeException();

        if(data.contains(name+".inherit")) {
            String o = data.getString(name+".inherit");
            try {
                OldPetType type = PetPlugin.getPetTypeByName(o);
                //System.out.println(type);
                setDisplayName(type.getDisplayName());
                this.skins = type.skins;
                this.description = type.description;
                this.ltype = type.ltype;
                this.distance = type.distance;
                this.price = type.price;
                this.animation = type.animation;
                this.entityType = type.entityType;
                this.mythicMob = type.mythicMob;
                this.abilities = type.abilities;
                this.namey = type.namey;

            } catch(Exception ignored) {
                Bukkit.getLogger().warning(name+" couldn't inherit from "+o);
            }
        }

        setName(name);

        if(data.contains(name+".displayname")) setDisplayName(Utils.color(data.getString(name+".displayname")));
        if(data.contains(name+".skins")) this.skins =  Utils.fromList(data.getStringList(name+".skins"));
        if(data.contains(name+".description")) this.description = Utils.color(data.getStringList(name+".description"));

        if(data.contains(name+".animation")) {
            this.animation = AnimationType.valueOf(data.getString(name+".animation"));
        }
        if(data.contains(name+".price")) {
            this.price = data.getDouble(name+".price");
        }
        if(data.contains(name+".visible")) {
            this.visible = data.getBoolean(name+".visible");
        }
        if(data.contains(name+".level.type")) {
            try {
                this.ltype = LType.valueOf(data.getString(name+".level.type"));
                this.ltype.setValue(data.getInt(name+".level.value"));
                if(this.ltype == LType.MINE) {
                    this.ltype.setObject(Material.valueOf(data.getString(name+".level.material")));
                }
                if(this.ltype == LType.KILL) {
                    this.ltype.setObject(EntityType.valueOf(data.getString(name+".level.entitytype")));
                }
            } catch(Exception ignored) {
                Bukkit.getLogger().info("Invalid level configuration, "+this.getName());
            }
        }

        if(data.contains(name+".group")) {
            this.group = data.getString(name+".group");
        }
        if(data.contains(name+".y")) {
            this.namey = data.getDouble(name+".y");
        }
        if(data.contains(name+".balloon")) {
            this.balloon = data.getBoolean(name+".balloon");
            if(data.contains(name+".balloon_height")) {
                this.ballon_height = data.getDouble(name+".balloon_height");
            }
        }
        if(data.contains(name+".yaw")) {
            this.yaw = data.getInt(name+".yaw");
        }
        if(this.skins[0].startsWith("MOB:") && !PetPlugin.getInstance().isUsingLegacySound()) {
            this.entityType = EntityType.valueOf(this.skins[0].replace("MOB:", ""));
            if(this.distance == 1) this.distance = 1.3;
        } else {
            if(this.skins[0].startsWith("MYTHICMOB:") && !PetPlugin.getInstance().isUsingLegacySound()) {
                this.mythicMob = this.skins[0].replace("MYTHICMOB:", "");
                if(this.distance == 1) this.distance = 1.3;
            } else {
                if(this.skins[0].startsWith("MODELENGINE:")) {
                    this.modelEngine = this.skins[0].replace("MODELENGINE:", "");
                    if(this.distance == 1) this.distance = 1.3;
                } else {
                    if(this.skins[0].contains(":") && !this.skins[0].contains("HDB:") && !PetPlugin.getInstance().isUsingLegacySound()) this.customModelData = true;
                    Material mat;
                    if(PetPlugin.getInstance().isUsingLegacyId()) {
                        mat = Material.LEGACY_SKULL_ITEM;
                    } else {
                        mat = Material.PLAYER_HEAD;
                    }
                    if(getIcon(null).getType() == mat) this.yaw = -PetPlugin.getInstance().getYawCalibration();
                }
            }
        }
        if(data.contains(name+".distance")) {
            this.distance = data.getDouble(name+".distance");
        }
        if(data.contains(name+".abilities")) {
            for(String s : data.getStringList(name+".abilities")) {
                abilities.add(new AbilityExecutor(s));
            }
        }
    }

    @Override
    public ItemStack getIcon(Player p) {
        return Utils.editStack(Utils.getCustomItem(getSkins()[0]), getDisplayName(), GUI.petLore(getDescription(), this, p));
    }

}
