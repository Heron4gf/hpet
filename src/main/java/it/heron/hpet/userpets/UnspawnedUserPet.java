package it.heron.hpet.userpets;

import it.heron.hpet.ChildPet;
import it.heron.hpet.pettypes.CosmeticType;
import it.heron.hpet.pettypes.PetType;
import lombok.Data;
import org.bukkit.Color;

import java.util.UUID;

public @Data class UnspawnedUserPet {

    private PetType type;
    private boolean child;
    private String name;
    private boolean glow;
    private UUID owner;
    private Color color = Color.WHITE;

    public UnspawnedUserPet(PetType type, UUID owner, boolean child, String name, boolean glow) {
        this.type = type;
        this.owner = owner;
        this.child = child;
        this.name = name;
        this.glow = glow;
    }

    public UnspawnedUserPet(PetType type, UUID owner, boolean child, String name, boolean glow, Color color) {
        this.type = type;
        this.owner = owner;
        this.child = child;
        this.name = name;
        this.glow = glow;
        this.color = color;
    }

    public UserPet toUserPet() {
        if(type == null) return null;
        UserPet userPet;
        if(child) {
            userPet = new UserPet(owner,type,new ChildPet());
        } else {
            if(type instanceof CosmeticType && ((CosmeticType)type).getGroup().equals("CHEST")) {
                userPet = new PassengerUserPet(owner,type,null);
            } else {
                userPet = new UserPet(owner,type,null);
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
