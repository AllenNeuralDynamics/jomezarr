package org.aind.omezarr;

import java.util.ArrayList;

public class OmeZarrIndexGenerator {

    private boolean hasT = false;
    private boolean hasC = false;
    private boolean hasZ = false;
    private boolean hasY = false;
    private boolean hasX = false;

    public OmeZarrIndexGenerator(ArrayList<OmeZarrAxis> axes) {
        hasX = true;
        hasY = true;

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
        return null;
    }
}
