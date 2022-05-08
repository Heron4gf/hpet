package it.heron.hpet.operations;

import lombok.Data;

public @Data class Sin {
    private int n;
    private double value;

    public static Sin get(int n) {
        Sin c = new Sin();
        c.setN(n);
        c.setValue(-Math.sin(Math.toRadians(n)));
        return c;
    }
}
