package it.heron.hpet.modules.pets;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.abstracts.AbstractModule;
import it.heron.hpet.modules.abstracts.DefaultInstanceModule;
import it.heron.hpet.modules.pets.pettypes.PetType;
import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;
import it.heron.hpet.modules.pets.userpets.workloads.WorkloadRunnable;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PetsHandler extends DefaultInstanceModule {

    private int taskId = -1;

    private Set<UserPet> spawnedPets = new HashSet<>();

    @Getter
    private WorkloadRunnable workloadRunnable = new WorkloadRunnable();

    @Override
    public String name() {
        return "PetsHandler";
    }

    @Override
    protected void onLoad() {
        startWorkloadRunnable();
    }

    @Override
    protected void onUnload() {
        endTask();
    }

    public Set<UserPet> userPets(UUID owner) {
        Set<UserPet> userPets = new HashSet<>();
        for(UserPet userPet : spawnedPets) {
            if(userPet.getOwner().equals(owner)) {
                userPets.add(userPet);
            }
        }
        return userPets;
    }

    public UserPet selectPet(Entity entity, PetType petType) {
        UserPet userPet = null;

        registerPet(userPet);
        return userPet;
    }

    public boolean isPetRegistered(UserPet userPet) {
        return this.spawnedPets.contains(userPet);
    }

    public Collection<UserPet> spawnedPets() {
        return spawnedPets;
    }

    public void removePet(UserPet userPet) {
        userPet.despawn();
        unregisterPet(userPet);
    }

    private void registerPet(UserPet userPet) {
        this.spawnedPets.add(userPet);
    }

    private void unregisterPet(UserPet userPet) {
        this.spawnedPets.remove(userPet);
    }


    private void endTask() {
        if(this.taskId == -1) return;
        Bukkit.getScheduler().cancelTask(this.taskId);
        this.taskId = -1;
    }

    private void startWorkloadRunnable() {
        this.taskId = Bukkit.getScheduler().runTaskTimer(PetPlugin.getInstance(), this.workloadRunnable, 1, 1).getTaskId();
    }

}
