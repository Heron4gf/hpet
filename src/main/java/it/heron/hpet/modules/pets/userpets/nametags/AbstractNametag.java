package it.heron.hpet.modules.pets.userpets.nametags;

import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;


public abstract @AllArgsConstructor class AbstractNametag implements INametag {

    /**
     * Constructs an AbstractNametag with the specified name component.
     *
     * @param name the display name component for the nametag
     */
    public AbstractNametag(Component name) {
        // do validation
    }

    /**
     * Displays the nametag if it is not already shown.
     */
    public void show() {
        if(isShown()) return;
    }

    public void hide() {
        if(!isShown()) return;
    }

}
