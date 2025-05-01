package it.heron.hpet.modules.pets.userpets.workloads;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.pets.PetsHandler;
import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;

public class UserPetsWorkload implements ScheduledWorkload {

    private UserPet userPet;

    public UserPetsWorkload(UserPet userPet) {
        this.userPet = userPet;
    }

    @Override
    public void compute() {
        this.userPet.tick();
    }

    /**
     * Determines whether the workload should be rescheduled based on the registration status of the associated pet.
     *
     * @return {@code true} if the pet is still registered and the workload should continue; {@code false} if the task is complete.
     */
    @Override
    public boolean shouldBeRescheduled() {
        PetsHandler handler = (PetsHandler) PetPlugin.getInstance().getModulesHandler().moduleByName("petshandler");
        return handler.isPetRegistered(this.userPet); // This task is complete after one execution
    }
}
