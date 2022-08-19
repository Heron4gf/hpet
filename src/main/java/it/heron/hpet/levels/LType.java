/*
 * This file is part of HPET - Packet Based Pet Plugin
 *
 * TOS (Terms of Service)
 * You are not allowed to decompile, or redestribuite part of this code if not authorized by the original author.
 * You are not allowed to claim this resource as yours.
 */
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
