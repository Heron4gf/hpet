package it.heron.hpet.animation;

import it.heron.hpet.Pet;
import it.heron.hpet.packetutils.versions.Utils_;

public enum AnimationType {
    GLIDE, BOUNCE, GLITCH, NONE, SLOW_GLIDE, WALK, SIDE, FOLLOW;

    private static float[] glide = {-0.3f, -0.25f, -0.2f, -0.1f, 0, 0.1f, 0.2f, 0.25f, 0.3f, 0.3f, 0.25f, 0.2f, 0.1f, 0, -0.1f, -0.2f, -0.25f, -0.3f};
    private static float[] slow_glide = {-0.3f, -0.3f, -0.25f, -0.25f, -0.2f, -0.2f, -0.1f, -0.1f, 0, 0, 0.1f, 0.1f, 0.2f, 0.2f, 0.25f, 0.25f, 0.3f, 0.3f, 0.3f, 0.3f, 0.25f, 0.25f, 0.2f, 0.2f, 0.1f, 0, 0, -0.1f, -0.1f, -0.2f, -0.2f, -0.25f, -0.25f, -0.3f, -0.3f};
    private static float[] bounce = {-0.3f, -0.15f, 0.1f, 0.2f, 0.25f, 0.3f, 0.3f, 0.25f, 0.2f, 0.1f, -0.15f};
    private static float[] glitch = {-0.3f, 0, -0.2f, 0.3f, -0.2f, 0f, 0.3f, -0.1f};

    public static void setConst() {
        if(!new Utils_().enable()) Pet.getInstance().setPetTypes(null);
        float c = 0.5f;
        for(int i = 0; i < slow_glide.length; i++) {
            if(i<glide.length) glide[i] = glide[i]+c;
            if(i<bounce.length) bounce[i] = bounce[i]+c;
            if(i<glitch.length) glitch[i] = glitch[i]+c;
            slow_glide[i] = slow_glide[i]+c;
        }
    }

    public float[] getAnimationValues() {

        switch(this) {
            case GLIDE:
                return glide;
            case NONE:
                return new float[]{0.5f};
            case GLITCH:
                return glitch;
            case BOUNCE:
                return bounce;
            case SLOW_GLIDE:
                return slow_glide;
            case SIDE:
                return new float[]{-0.9f};
            case WALK:
                return new float[]{-123};
            case FOLLOW:
                return new float[]{0.5f};
            default:
                return glide;
        }
    }
}
