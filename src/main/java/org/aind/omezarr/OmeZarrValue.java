package org.aind.omezarr;

import java.util.ArrayList;

public class OmeZarrValue {

    public static final double InvalidPosition = Double.MIN_VALUE;

    public static final OmeZarrValue InvalidValue = new OmeZarrValue(InvalidPosition, InvalidPosition, InvalidPosition, InvalidPosition, InvalidPosition);

    private final double t;

    private final double c;

    private final double x;

    private final double y;

    private final double z;

    private final double[] array;

    public OmeZarrValue(double y, double x) {
        this(InvalidPosition, InvalidPosition, InvalidPosition, y, x);
    }

    public OmeZarrValue(double z, double y, double x) {
        this(InvalidPosition, InvalidPosition, z, y, x);
    }

    public OmeZarrValue(double t, double c, double z, double y, double x) {
        this.t = t;
        this.c = c;
        this.x = x;
        this.y = y;
        this.z = z;

        ArrayList<Double> values = new ArrayList<>();

        if (t > InvalidPosition) {
            values.add(t);
        }

        if (c > InvalidPosition) {
            values.add(c);
        }

        if (z > InvalidPosition) {
            values.add(z);
        }

        if (y > InvalidPosition) {
            values.add(y);
        }

        if (x > InvalidPosition) {
            values.add(x);
        }

        array = values.stream().mapToDouble(i -> i).toArray();
    }

    public double[] get(){
        return array;
    }

    public double getT() {
        return t;
    }

    public double getC() {
        return c;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
