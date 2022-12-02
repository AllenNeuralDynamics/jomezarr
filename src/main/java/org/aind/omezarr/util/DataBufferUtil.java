package org.aind.omezarr.util;

import java.awt.image.DataBufferUShort;

public class DataBufferUtil {
    public static SimpleStatistics createStatistics(DataBufferUShort buffer) {
        int min = buffer.getElem(0);
        int max = buffer.getElem(0);
        int length = buffer.getSize();

        double sum = 0.0;

        for (int idx = 1; idx < length; idx++) {
            int elem = buffer.getElem(idx);
            min = Math.min(min, elem);
            max = Math.max(max,elem);
            sum += elem;
        }

        double mean = sum / length;
        double stdDev = 0.0;

        for (int idx = 1; idx < length; idx++) {
            stdDev += Math.pow(buffer.getElem(idx) - mean, 2);
        }

        stdDev = Math.sqrt(stdDev / length);

        return new SimpleStatistics(min, max, mean, stdDev);
    }
}
