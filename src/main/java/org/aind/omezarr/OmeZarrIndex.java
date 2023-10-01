package org.aind.omezarr;

import java.util.ArrayList;

public class OmeZarrIndex {

    public static final int InvalidPosition = -1;

    public static final OmeZarrIndex InvalidIndex = new OmeZarrIndex(InvalidPosition, InvalidPosition, InvalidPosition, InvalidPosition, InvalidPosition);

    private final int t;

    private final int c;

    private final int x;

    private final int y;

    private final int z;

    private final int[] array;

    public OmeZarrIndex(int y, int x) {
        this(InvalidPosition, InvalidPosition, InvalidPosition, y, x);
    }

    public OmeZarrIndex(int z, int y, int x) {
        this(InvalidPosition, InvalidPosition, z, y, x);
    }

    public OmeZarrIndex(int t, int c, int z, int y, int x) {
        this.t = t;
        this.c = c;
        this.x = x;
        this.y = y;
        this.z = z;

        ArrayList<Integer> values = new ArrayList<>();

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

        array = values.stream().mapToInt(i -> i).toArray();
    }

    public int[] get(){
        return array;
    }

    public int getT() {
        return t;
    }

    public int getC() {
        return c;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
