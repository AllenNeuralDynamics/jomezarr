package org.aind.omezarr.zarr;

import com.bc.zarr.CompressorFactory;

public class ZarrProperties {
    public static void setBloscCompressorThreads(int numThreads) {
        CompressorFactory.BloscCompressor.defaultProperties.put("nthreads", numThreads);
    }
}
