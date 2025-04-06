package it.heron.hpet.modules.pets.userpets.animations.abstracts;

public abstract class AbstractAnimation implements IAnimation {

    protected long step = -1; // starting state

    @Override
    public void nextStep() {
        this.step++;
    }

    protected abstract int runEvery();
}
