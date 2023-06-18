package it.heron.hpet.operations;

import lombok.Data;
import org.bukkit.Location;

public @Data
class Coords {
    private Cos cos;
    private Sin sin;
    private double distance = 0.9;
    private double y = 1;

    public static Coords calculate(int n, double distance, double y) {
        n = n-35;
        Coords c = new Coords();
        c.setCos(Cos.get(n));
        c.setSin(Sin.get(n));
        c.setDistance(distance);
        c.setY(y);
        return c;
    }

    public Location getLoc(Location loc) {
        return new Location(loc.getWorld(), loc.getX()+(cos.getValue()*distance), loc.getY()+this.y, loc.getZ()+(sin.getValue()*distance),loc.getYaw(),loc.getPitch());
    }
}
