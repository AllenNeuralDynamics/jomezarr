package org.aind.omezarr.util;

public class SimpleStatistics {
    public int min;

    public int max;

    public double mean;

    public double stdDev;

    public SimpleStatistics(int min, int max, double mean, double stdDev) {
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.stdDev = stdDev;
    }
}
