package it.heron.hpet.operations;

import lombok.Data;

public @Data class Cos {
    private int n;
    private double value;

    public static Cos get(int n) {
        Cos c = new Cos();
        c.setN(n);
        c.setValue(-Math.cos(Math.toRadians(n)));
        return c;
    }
}
