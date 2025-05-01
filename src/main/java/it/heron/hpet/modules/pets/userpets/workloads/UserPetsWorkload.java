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

    @Override
    public boolean shouldBeRescheduled() {
        PetsHandler handler = (PetsHandler) PetPlugin.getInstance().getModulesHandler().moduleByName("petshandler");
        return handler.isPetRegistered(this.userPet); // This task is complete after one execution
    }
}
