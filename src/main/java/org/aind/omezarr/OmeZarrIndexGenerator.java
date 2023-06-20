package org.aind.omezarr;

import java.util.ArrayList;

public class OmeZarrIndexGenerator {
    private boolean hasT = false;
    private boolean hasC = false;
    private boolean hasZ = false;

    public OmeZarrIndexGenerator(ArrayList<OmeZarrAxis> axes) {
        int spatialCount = 0;

        for (OmeZarrAxis axis: axes) {
            if (axis.getType() == OmeZarrAxisType.TIME) {
                hasT = true;
            }
            else if (axis.getType() == OmeZarrAxisType.CHANNEL) {
                hasC = true;
            } else {
                spatialCount++;
            }
        }

        if (spatialCount > 2) {
            hasZ = true;
        }
    }

    public OmeZarrIndex createIndex(int[] values) {
        int t = hasT ? values[0] : -1;

        int c = hasC ? (hasT ? values[1] : values[0]) : -1;

        int offset = hasC ? (hasT ? 2 : 1) : 0;

        int z = hasZ ? values[offset++] : -1;

        int y = values[offset++];

        int x = values[offset];

        return new OmeZarrIndex(t, c, z, y, x);
    }
}
