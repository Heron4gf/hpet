package it.heron.hpet.modules.pets.pettypes;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.messages.ComponentsHelper;
import lombok.Data;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public abstract @Data class AbstractPetType implements PetType {

    private YamlConfiguration configuration;

    private String name;

    private Component displayName;
    private List<Component> description;

    private Vector relativeLocation = new Vector(0, 0, 0);
    private Vector nametagRelativeLocation = new Vector(0, 0, 0);
    private Double price = null;
    private String animationName = "glide";

    private float yaw = 200f;
    private double distance = 0d;

    public AbstractPetType(YamlConfiguration configuration, @NonNull String key) {
        this.configuration = configuration;
        this.name = key;
        this.displayName = ComponentsHelper.simpleParse(configuration.getString(absolutePath("displayname")));
        this.description = ComponentsHelper.listParse(configuration.getStringList(absolutePath("description")));

        loadVector(null, relativeLocation);
        loadVector("nametag", nametagRelativeLocation);
        this.price = getNullableDouble("price");
        this.animationName = configuration.getString(absolutePath("animation"), animationName);
        this.yaw = (float) configuration.getDouble(absolutePath("yaw"), yaw);
        this.distance = configuration.getDouble(absolutePath("distance"), distance);
    }

    @Override
    public boolean canSee(Player player) {
        return player.hasPermission("pet.see."+this.name);
    }

    @Override
    public boolean isUnlocked(Player player) {
        return player.hasPermission("pet.use."+this.name) || bought(player);
    }

    @Override
    public boolean bought(Player player) {
        return PetPlugin.getInstance().getDatabase().hasBought(player, this);
    }

    protected void loadVector(String subpath, Vector def) {
        String path = absolutePath(subpath);
        double x = configuration.getDouble(path, def.getX());
        double y = configuration.getDouble(path, def.getY());
        double z = configuration.getDouble(path, def.getZ());
        def.setX(x);
        def.setY(y);
        def.setZ(z);
    }

    protected Double getNullableDouble(String subpath) {
        if(!configuration.contains(absolutePath(subpath))) return null;
        return configuration.getDouble(absolutePath(subpath));
    }

    protected String absolutePath(String subpath) {
        if(subpath == null || subpath.isEmpty()) return this.name;
        return this.name+"."+subpath;
    }

}
