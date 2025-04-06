package it.heron.hpet.modules.pets.userpets.animations.abstracts;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public abstract class UpDownAbstractAnimation extends AbstractAnimation {

    // Define a default/zero offset location constant for clarity
    private static final Vector ZERO_OFFSET = new Vector(0, 0, 0);

    /**
     * Defines the sequence of height offsets (Y-coordinate modifiers) for the animation.
     * @return An array of float values representing height offsets. Should not be null or empty.
     */
    protected abstract float[] heightModifiers();

    /**
     * Determines how many animation steps pass before switching to the next height modifier.
     * @return The number of steps per height change. Should be greater than 0.
     */
    @Override
    protected int runEvery() {
        return 3;
    }

    /**
     * Calculates the relative location offset based on the current animation step
     * and the defined height modifiers, cycling through them.
     * @return A Location representing the relative offset (primarily Y-axis).
     *         Returns a zero offset if input parameters (runEvery, heightModifiers) are invalid.
     */
    @Override
    public Vector relativeLocation(Location ownerLocation) {
        int updateFrequency = runEvery();
        float[] modifiers = heightModifiers();

        if (updateFrequency <= 0) {
            return ZERO_OFFSET; // Return a safe default
        }
        if (modifiers == null || modifiers.length == 0) {
            return ZERO_OFFSET; // Return a safe default
        }

        long currentStep = this.step;
        int intervalCount = (int) (currentStep / updateFrequency);
        int index = intervalCount % modifiers.length;

        if (index < 0) {
            index += modifiers.length;
        }
        float heightModifier = modifiers[index];
        return new Vector(0, heightModifier, 0);
    }

}