/*
 * This file is part of HPET - Packet Based Pet Plugin
 *
 * TOS (Terms of Service)
 * You are not allowed to decompile, or redestribuite part of this code if not authorized by the original author.
 * You are not allowed to claim this resource as yours.
 */
package it.heron.hpet.userpets.childpet;

import it.heron.hpet.main.PetPlugin;
import lombok.Data;
import org.bukkit.Location;

public @Data
class ChildPet {
    private int id;
    private long step = 0;

    public ChildPet() {}

    public void teleport(Location newLoc, float[] steps) {
        newLoc.setYaw((this.step%18)*20-180);
        newLoc.add(0, 0.5+steps[(int) (this.step%steps.length)], 0);
        PetPlugin.getInstance().getPacketUtils().executePacket(PetPlugin.getInstance().getPacketUtils().teleportEntity(this.id, newLoc, true), newLoc.getWorld());
        this.step++;
    }
}
