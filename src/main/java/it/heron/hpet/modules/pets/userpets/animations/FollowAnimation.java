package it.heron.hpet.modules.pets.userpets.animations; // Assuming this is the correct package

import it.heron.hpet.modules.pets.userpets.animations.abstracts.AbstractAnimation;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class FollowAnimation extends AbstractAnimation {

    private static final double DISTANCE_BEHIND = 1.5;
    private static final double DISTANCE_SIDEWAYS = 0.8;
    private static final double HEIGHT_OFFSET = -0.2;
    private static final int RUN_EVERY_TICKS = 1;

    @Override
    public String name() {
        return "follow";
    }

    @Override
    protected int runEvery() {
        return RUN_EVERY_TICKS;
    }

    /**
     * Calculates the desired pet location relative to the owner.
     * The pet will try to stay behind and to the side of the owner.
     *
     * @param ownerLocation The current location of the owner.
     * @return A Vector representing the desired offset from the owner's location.
     */
    @Override
    public Vector relativeLocation(Location ownerLocation) {
        Vector ownerDirection = ownerLocation.getDirection().normalize();

        Vector behindVector = ownerDirection.clone().multiply(-DISTANCE_BEHIND);
        Vector rightVector = ownerDirection.clone().crossProduct(new Vector(0, 1, 0)).normalize();

        if (rightVector.lengthSquared() == 0) {
            Location tempLoc = ownerLocation.clone();
            tempLoc.setPitch(0);
            rightVector = tempLoc.getDirection().crossProduct(new Vector(0, 1, 0)).normalize();
            if (rightVector.lengthSquared() == 0) {
                rightVector = new Vector(1, 0, 0);
            }
        }


        double sideMultiplier = DISTANCE_SIDEWAYS; // Simple fixed side
        Vector sidewaysVector = rightVector.multiply(sideMultiplier);
        Vector heightVector = new Vector(0, HEIGHT_OFFSET, 0);
        Vector finalOffset = behindVector.add(sidewaysVector).add(heightVector);

        return finalOffset;
    }
}