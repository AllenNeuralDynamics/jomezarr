package org.aind.omezarr;

import java.util.ArrayList;

public class OmeZarrIndex {

    public static OmeZarrIndex InvalidIndex = new OmeZarrIndex(-1, -1, -1, -1, -1);

    private final int t;

    private final int c;

    private final int x;

    private final int y;

    private final int z;

    private final int[] array;

    public OmeZarrIndex(int y, int x) {
        this(-1, -1, -1, y, x);
    }

    public OmeZarrIndex(int t, int c, int z, int y, int x) {
        this.t = t;
        this.c = c;
        this.x = x;
        this.y = y;
        this.z = z;

        ArrayList<Integer> values = new ArrayList<>();

        if (t >= 0) {
            values.add(t);
        }

        if (c >= 0) {
            values.add(c);
        }

        if (z >= 0) {
            values.add(z);
        }

        if (y >= 0) {
            values.add(y);
        }

        if (x >= 0) {
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
