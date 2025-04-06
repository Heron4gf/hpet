package it.heron.hpet.modules.pets.userpets.animations;

import it.heron.hpet.modules.pets.userpets.animations.abstracts.UpDownAbstractAnimation;

public class SlowGlideAnimation extends UpDownAbstractAnimation {
    @Override
    protected float[] heightModifiers() {
        return new float[]{-0.3f, -0.3f, -0.25f, -0.25f, -0.2f, -0.2f, -0.1f, -0.1f, 0, 0, 0.1f, 0.1f, 0.2f, 0.2f, 0.25f, 0.25f, 0.3f, 0.3f, 0.3f, 0.3f, 0.25f, 0.25f, 0.2f, 0.2f, 0.1f, 0, 0, -0.1f, -0.1f, -0.2f, -0.2f, -0.25f, -0.25f, -0.3f, -0.3f};
    }

    @Override
    public String name() {
        return "slow_glide";
    }
}
