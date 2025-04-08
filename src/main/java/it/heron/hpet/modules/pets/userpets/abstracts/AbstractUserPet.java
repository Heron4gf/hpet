package it.heron.hpet.modules.pets.userpets.abstracts;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.modules.abilities.abstracts.Ability;
import it.heron.hpet.modules.invisibilityintegration.InvisibilityHandler;
import it.heron.hpet.modules.pets.pettypes.PetType;
import it.heron.hpet.modules.pets.userpets.animations.abstracts.IAnimation;
import it.heron.hpet.modules.pets.userpets.nametags.INametag;
import it.heron.hpet.modules.pets.userpets.nametags.NametagGenerator;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.UUID;

public abstract class AbstractUserPet implements UserPet {

    private static final int UNSPAWNED_ID = -1;

    @Getter
    protected Location location;
    @Getter
    protected UUID owner;
    @Getter @Setter
    protected int level;
    @Getter
    protected PetType petType;
    @Getter
    protected boolean visible = true; // doesn't affect vanish, this value will be true if pet is vanished
    @Getter
    protected boolean vanished = false;
    @Getter
    protected int id = -1;
    @Getter
    protected IAnimation animation;
    @Getter
    protected INametag nametag;

    private boolean currentVisibilityState = true; // current visibility state, shouldn't be used externally

    public AbstractUserPet(@NonNull PetType petType, @NonNull Entity owner, int level) {
        this.petType = petType;
        this.owner = owner.getUniqueId();
        this.level = level;
        this.nametag = NametagGenerator.getFormattedNametag(getPetType().getName());
    }

    @Override
    public void teleport(Location location) {
        if(!currentVisibilityState) return;
        if(this.location.equals(location)) return;
    }

    @Override
    public boolean isSpawned() {
        return this.id != UNSPAWNED_ID;
    }

    @Override
    public void spawn() {
        if(isSpawned()) despawn();
        nametag.show();
        onSpawn();
        if(this.id == UNSPAWNED_ID) throw new RuntimeException("There was an error while spawning the Pet");
    }

    @Override
    public void despawn() {
        if(!isSpawned()) return;
        nametag.hide();
        onDespawn();
        this.id = UNSPAWNED_ID;
    }

    @Override
    public void tick() {
        Entity ownerEntity = Bukkit.getEntity(this.owner);
        InvisibilityHandler handler = (InvisibilityHandler) PetPlugin.getInstance().getModulesHandler().moduleByName("Vanish");
        this.vanished = handler.isInvisible(ownerEntity);

        applyVisibilityState(!this.vanished && this.visible);
        teleport(getNextLocation());

        if(petType.getAbility() != null) {
            petType.getAbility().execute(this);
        }
    }

    @Override
    public void rename(String name) {
        NametagGenerator.changeNametagFormatted(nametag, name);
    }

    @Override
    public void setVisible(boolean state) {
        this.visible = state;
        applyVisibilityState(this.visible && !this.vanished);
    }

    protected Location getNextLocation() {
        animation.nextStep();
        Location ownerLocation = Bukkit.getEntity(owner).getLocation();
        Vector relativeLocation = animation.relativeLocation(ownerLocation);
        return this.location.clone().add(relativeLocation);
    }

    private void applyVisibilityState(boolean state) {
        if(this.currentVisibilityState == state) return;
        if(state) {
            spawn();
            this.currentVisibilityState = true;
        } else {
            despawn();
            this.currentVisibilityState = false;
        }
    }

    protected abstract void onSpawn();
    protected abstract void onDespawn();

}
