package it.heron.hpet.levels;

import lombok.Getter;
import lombok.Setter;

public enum LType {
    MINE, WALK, KILL, JUMP, NONE, CAKE_EATEN, DEATHS, PLAYER_KILLS;

    @Getter @Setter
    private int value = 0;

    @Getter @Setter
    private Object object;
}
