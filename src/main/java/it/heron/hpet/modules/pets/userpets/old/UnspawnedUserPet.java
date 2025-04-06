package it.heron.hpet.modules.pets.userpets.old;

import it.heron.hpet.pets.pettypes.CosmeticType;
import it.heron.hpet.modules.pets.pettypes.OldPetType;
import it.heron.hpet.userpets.childpet.ChildPet;
import lombok.Data;
import org.bukkit.Color;

import java.util.UUID;

public @Data class UnspawnedUserPet {

    private OldPetType type;
    private boolean child;
    private String name;
    private boolean glow;
    private UUID owner;
    private Color color = Color.WHITE;

    public UnspawnedUserPet(OldPetType type, UUID owner, boolean child, String name, boolean glow) {
        this.type = type;
        this.owner = owner;
        this.child = child;
        this.name = name;
        this.glow = glow;
    }

    public UnspawnedUserPet(OldPetType type, UUID owner, boolean child, String name, boolean glow, Color color) {
        this.type = type;
        this.owner = owner;
        this.child = child;
        this.name = name;
        this.glow = glow;
        this.color = color;
    }

    public HeadUserPet toUserPet() {
        if(type == null) return null;
        HeadUserPet userPet;
        if(child) {
            userPet = new HeadUserPet(owner,type,new ChildPet());
        } else {
            if(type instanceof CosmeticType && ((CosmeticType)type).getGroup().equals("CHEST")) {
                userPet = new PassengerUserPet(owner,type,null);
            } else {
                userPet = new HeadUserPet(owner,type,null);
            }
        }
        if(name != null) {
            userPet.setName(name);
        }
        userPet.setColor(color);
        userPet.setGlow(glow);
        return userPet;
    }

}
