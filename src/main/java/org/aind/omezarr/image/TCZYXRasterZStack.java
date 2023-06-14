package org.aind.omezarr.image;

import org.aind.omezarr.OmeZarrDataset;
import org.aind.omezarr.util.PerformanceMetrics;
import ucar.ma2.InvalidRangeException;

import java.awt.*;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class TCZYXRasterZStack {
    public static AutoContrastParameters computeAutoContrast(OmeZarrDataset dataset, int[] shape) throws IOException, InvalidRangeException {
        if (dataset == null || !dataset.isValid()) {
            throw new IllegalArgumentException("dataset");
        }

        if (shape.length < 5) {
            throw new IllegalArgumentException("shape");
        }

        int[] fullSize = dataset.getShape();

        int[] actualShape = new int[fullSize.length];
        int[] actualOffset = new int[fullSize.length];

        for (int idx = 0; idx < shape.length; idx++) {
            int mid = (int) (fullSize[idx] / 2.0);
            int half = (int) (shape[idx] / 2.0);
            actualOffset[idx] = Math.max(0, Math.min(shape[idx] - 1, mid - half));
            actualShape[idx] = Math.max(1, Math.min(fullSize[idx] - 1, shape[idx]));
        }

        short[] data = dataset.readShort(actualShape, actualOffset);

        DataBuffer buffer;

        if (!dataset.getIsUnsigned()) {
            buffer = fromSignedShort(data, data.length, 0);
        } else {
            buffer = new DataBufferUShort(data, data.length, 0);
        }

        return AutoContrastParameters.fromBuffer(buffer);
    }

    public static WritableRaster[] fromDataset(OmeZarrDataset dataset, int[] shape, int[] offset, AutoContrastParameters parameters) {
        return fromDataset(dataset, shape, offset, 1, parameters != null, parameters, null);
    }

    public static WritableRaster[] fromDataset(OmeZarrDataset dataset, int[] shape, int[] offset, int numTasks, boolean autoContrast, AutoContrastParameters parameters, PerformanceMetrics metrics) {
        if (dataset == null || !dataset.isValid()) {
            throw new IllegalArgumentException("dataset");
        }

        if (shape.length < 5) {
            throw new IllegalArgumentException("shape");
        }

        if (offset.length < 5) {
            throw new IllegalArgumentException("offset");
        }

        try {
            ArrayList<DataBufferUShort> dataBuffers;

            dataBuffers = createDataBuffers(dataset, shape, offset, numTasks, metrics);

            if (autoContrast && parameters == null) {
                int idx = (int) (dataBuffers.size() / 2.0);

                Instant start = Instant.now();

                parameters = AutoContrastParameters.fromBuffer(dataBuffers.get(idx));

                if (metrics != null) {
                    metrics.autoConstrastDuration = Duration.between(start, Instant.now());
                }
            }

            return dataBufferToRaster(dataBuffers, shape[4], shape[3], parameters, metrics);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    private static ArrayList<DataBufferUShort> createDataBuffers(OmeZarrDataset dataset, int[] shape, int[] offset, int numTasks, PerformanceMetrics metrics) throws InvalidRangeException, IOException {
        int count = shape[2];

        Instant start = Instant.now();

        boolean isUnsigned = dataset.getIsUnsigned();

        short[] data = numTasks > 1 ? dataset.readShortAsParallel(shape, offset, numTasks) : dataset.readShort(shape, offset);

        if (metrics != null) {
            metrics.readDuration = Duration.between(start, Instant.now());
        }

        start = Instant.now();

        int zPlaneLength = shape[3] * shape[4];

        ArrayList<DataBufferUShort> dataBuffers = new ArrayList<>();

        for (int zIdx = 0; zIdx < count; zIdx++) {
            short[] subset = new short[zPlaneLength];

            System.arraycopy(data, zIdx * zPlaneLength, subset, 0, zPlaneLength);

            if (!isUnsigned) {
                dataBuffers.add(fromSignedShort(subset, subset.length, 0));
            } else {
                dataBuffers.add(new DataBufferUShort(subset, subset.length, 0));
            }
        }

        if (metrics != null) {
            metrics.dataBufferDuration = Duration.between(start, Instant.now());
        }

        return dataBuffers;
    }

    private static WritableRaster[] dataBufferToRaster(ArrayList<DataBufferUShort> dataBuffers, int width, int height, AutoContrastParameters parameters, PerformanceMetrics metrics) {
        WritableRaster[] rasterImages = new WritableRaster[dataBuffers.size()];

        for (int idx = 0; idx < rasterImages.length; idx++) {
            rasterImages[idx] = asWritableRaster(dataBuffers.get(idx), width, height, parameters, metrics);
        }

        return rasterImages;
    }

    private static WritableRaster asWritableRaster(DataBuffer buffer, int width, int height, AutoContrastParameters parameters, PerformanceMetrics metrics) {
        if (parameters != null) {
            Instant start = Instant.now();

            AutoContrast.apply(buffer, parameters);

            if (metrics != null) {
                metrics.autoConstrastDuration = metrics.autoConstrastDuration.plus(Duration.between(start, Instant.now()));
            }
        }

        Instant start = Instant.now();

        WritableRaster raster = Raster.createInterleavedRaster(buffer, width, height, width, 1, new int[]{0}, new Point(0, 0));

        if (metrics != null) {
            metrics.rasterizeDuration = metrics.rasterizeDuration.plus(Duration.between(start, Instant.now()));
        }

        return raster;
    }

    private static DataBufferUShort fromSignedShort(short[] data, int length, int offset) {
        for (int idx = offset; idx < offset + length; idx++) {
            data[idx] = (short) (data[idx] + 32768);
        }

        return new DataBufferUShort(data, length, offset);
    }
}
