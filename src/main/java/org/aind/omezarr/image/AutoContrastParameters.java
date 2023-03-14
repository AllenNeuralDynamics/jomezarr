package org.aind.omezarr.image;

import org.aind.omezarr.util.DataBufferUtil;
import org.aind.omezarr.util.SimpleStatistics;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;

public class AutoContrastParameters {
    public double min;

    public double slope;

    public static AutoContrastParameters fromBuffer(DataBuffer dataBuffer) {
        if (dataBuffer.getDataType() != DataBuffer.TYPE_USHORT) {
            throw new IllegalArgumentException("DataBuffer");
        }

        DataBufferUShort buffer = (DataBufferUShort) dataBuffer;

        SimpleStatistics stats = DataBufferUtil.createStatistics(buffer);

        // Crude placeholder.
        int min = (int) Math.max(stats.min, stats.mean - 4 * stats.stdDev);
        int max = (int) Math.min(stats.max, stats.mean + 4 * stats.stdDev);

        double slope = 65535.0 / (max - min);

        return new AutoContrastParameters(min, slope);
    }

    public AutoContrastParameters(double min, double slope) {
        this.min = min;
        this.slope = slope;
    }

    public void update(double min, double max) {
        this.min = min;

        this.slope = 65535.0 / (max - min);
    }

    public double getMax() {
        return min + (65535.0 / slope);
    }
}
