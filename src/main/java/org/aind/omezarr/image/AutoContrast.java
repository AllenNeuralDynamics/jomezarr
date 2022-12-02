package org.aind.omezarr.image;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;

import org.aind.omezarr.util.DataBufferUtil;
import org.aind.omezarr.util.SimpleStatistics;

public class AutoContrast {
    public static void apply(DataBuffer dataBuffer) {
        if (dataBuffer.getDataType() != DataBuffer.TYPE_USHORT) {
            throw new IllegalArgumentException("DataBuffer");
        }

        DataBufferUShort buffer = (DataBufferUShort) dataBuffer;

        SimpleStatistics stats = DataBufferUtil.createStatistics(buffer);

        // Crude placeholder.
        int min = (int) Math.max(stats.min, stats.mean - 4 * stats.stdDev);
        int max = (int) Math.min(stats.max, stats.mean + 4 * stats.stdDev);

        double slope = 65535.0 / (max - min);

        for (int idx = 0; idx < buffer.getSize(); idx++) {
            int val = buffer.getElem(idx);

            val = (int) ((val - min) * slope);

            if (val < 0) {
                val = 0;
            } else if (val > 65535) {
                val = 65535;
            }

            buffer.setElem(idx, val);
        }
    }
}
