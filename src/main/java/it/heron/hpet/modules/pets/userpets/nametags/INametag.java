package it.heron.hpet.modules.pets.userpets.nametags;

import net.kyori.adventure.text.Component;
import org.bukkit.util.Vector;

public interface INametag {

    void setName(Component name);
    Component getName();

    Vector relativeLocation();

    boolean isShown();
    void show();
    void hide();
}
