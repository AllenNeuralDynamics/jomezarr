package org.aind.omezarr;

public class OmeZarrReadChunk {
    private final int[] shape;

    private final int[] offset;

    public OmeZarrReadChunk(int[] shape, int[] offset) {
        this.shape = shape;
        this.offset = offset;
    }

    public int[] getShape() {
        return shape;
    }

    public int[] getOffset() {
        return offset;
    }
}
