package org.aind.omezarr.util;

import java.time.Duration;

public class PerformanceMetrics {
    public Duration readDuration = Duration.ZERO;

    public Duration dataBufferDuration = Duration.ZERO;

    public Duration autoConstrastDuration = Duration.ZERO;

    public Duration rasterizeDuration = Duration.ZERO;
}
