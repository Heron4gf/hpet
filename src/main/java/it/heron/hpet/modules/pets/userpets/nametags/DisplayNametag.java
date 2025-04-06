package it.heron.hpet.modules.pets.userpets.nametags;

import it.heron.hpet.modules.pets.userpets.fakeentities.FakeTextDisplay;
import net.kyori.adventure.text.Component;

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

    @Override
    public Component getName() {
        return textDisplay.getText();
    }

    @Override
    public boolean isShown() {
        return textDisplay != null && textDisplay.isSpawned();
    }
}
