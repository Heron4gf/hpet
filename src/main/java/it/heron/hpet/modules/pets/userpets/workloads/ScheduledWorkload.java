package it.heron.hpet.modules.pets.userpets.workloads;

public interface ScheduledWorkload {

    void compute();

    default boolean shouldBeRescheduled() {
        return false;
    }
}
