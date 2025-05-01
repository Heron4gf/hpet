package it.heron.hpet.modules.pets.userpets.nametags;

import it.heron.hpet.modules.pets.userpets.fakeentities.FakeTextDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

public class DisplayNametag extends AbstractNametag {
    private FakeTextDisplay textDisplay = null;

    public DisplayNametag(Component name) {
        super(name);
        textDisplay = new FakeTextDisplay(name);
    }

    @Override
    public void setName(Component name) {
        textDisplay.setText(name);
    }

    /**
     * Retrieves the current text displayed by the nametag.
     *
     * @return the current nametag text as a {@link Component}
     */
    @Override
    public Component getName() {
        return textDisplay.getText();
    }

    /**
     * Moves the nametag display to the specified location.
     *
     * @param location the new location for the nametag display
     */
    @Override
    public void teleport(Location location) {
        textDisplay.teleport(location, false);
    }

    /**
     * Checks if the nametag display is currently visible.
     *
     * @return {@code true} if the underlying text display exists and is spawned; {@code false} otherwise
     */
    @Override
    public boolean isShown() {
        return textDisplay != null && textDisplay.isSpawned();
    }
}
