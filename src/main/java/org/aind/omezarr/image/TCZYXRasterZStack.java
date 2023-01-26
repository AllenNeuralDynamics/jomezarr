package org.aind.omezarr.image;

import org.aind.omezarr.OmeZarrDataset;
import ucar.ma2.InvalidRangeException;

import java.awt.*;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;

public class TCZYXRasterZStack {
    public static AutoContrastParameters computeAutoContrast(OmeZarrDataset dataset, int[] shape) {
        if (dataset == null || !dataset.isValid()) {
            throw new IllegalArgumentException("dataset");
        }

        if (shape.length < 5) {
            throw new IllegalArgumentException("shape");
        }

        try {
            int[] fullSize = dataset.getShape();

            int[] actualShape = new int[fullSize.length];
            int[] actualOffset = new int[fullSize.length];

            for (int idx = 0; idx < shape.length; idx++) {
                int mid = (int) (fullSize[idx] / 2.0);
                int half = (int) (shape[idx] / 2.0);
                actualOffset[idx] = Math.max(0, Math.min(shape[idx] - 1, mid - half));
                actualShape[idx] = Math.min(fullSize[idx] - 1, shape[idx]);
            }

            short[] data = dataset.readShort(actualShape, actualOffset);

            DataBuffer buffer;

            if (!dataset.getIsUnsigned()) {
                buffer = fromSignedShort(data, data.length, 0);
            } else {
                buffer = new DataBufferUShort(data, data.length, 0);
            }

            return AutoContrastParameters.fromBuffer(buffer);
        } catch (Exception ex) {
            // TODO Don't absorb exception
            return null;
        }
    }

    public static WritableRaster[] fromDataset(OmeZarrDataset dataset, int[] shape, int[] offset, AutoContrastParameters parameters) {
        return fromDataset(dataset, shape, offset, parameters != null, parameters, false);
    }

    public static WritableRaster[] fromDataset(OmeZarrDataset dataset, int[] shape, int[] offset, boolean autoContrast, AutoContrastParameters parameters, boolean useSlowMethod) {
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

            if (useSlowMethod) {
                dataBuffers = createDataBuffersSlow(dataset, shape, offset);
            } else {
                dataBuffers = createDataBuffers(dataset, shape, offset);
            }

            if (autoContrast && parameters == null) {
                int idx = (int) (dataBuffers.size() / 2.0);

                parameters = AutoContrastParameters.fromBuffer(dataBuffers.get(idx));
            }

            return dataBufferToRaster(dataBuffers, shape[4], shape[3], parameters);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    private static ArrayList<DataBufferUShort> createDataBuffers(OmeZarrDataset dataset, int[] shape, int[] offset) throws InvalidRangeException, IOException {
        int count = shape[2];

        short[] data = dataset.readShort(shape, offset);

        int zPlaneLength = shape[3] * shape[4];

        ArrayList<DataBufferUShort> dataBuffers = new ArrayList<>();

        for (int zIdx = 0; zIdx < count; zIdx++) {
            short[] subset = new short[zPlaneLength];

            System.arraycopy(data, zIdx * zPlaneLength, subset, 0, zPlaneLength);

            if (!dataset.getIsUnsigned()) {
                dataBuffers.add(fromSignedShort(subset, subset.length, 0));
            } else {
                dataBuffers.add(new DataBufferUShort(subset, subset.length, 0));
            }
        }

        return dataBuffers;
    }

    private static ArrayList<DataBufferUShort> createDataBuffersSlow(OmeZarrDataset dataset, int[] shape, int[] offset) throws InvalidRangeException, IOException {
        int count = shape[2];

        shape[2] = 1;

        ArrayList<DataBufferUShort> dataBuffers = new ArrayList<>();

        for (int zIdx = 0; zIdx < count; zIdx++) {
            short[] data = dataset.readShort(shape, offset);

            if (!dataset.getIsUnsigned()) {
                dataBuffers.add(fromSignedShort(data, data.length, 0));
            } else {
                dataBuffers.add(new DataBufferUShort(data, data.length, 0));
            }

            offset[2] += 1;
        }

        return dataBuffers;
    }

    private static WritableRaster[] dataBufferToRaster(ArrayList<DataBufferUShort> dataBuffers, int width, int height, AutoContrastParameters parameters) {
        WritableRaster[] rasterImages = new WritableRaster[dataBuffers.size()];

        for (int idx = 0; idx < rasterImages.length; idx++) {
            rasterImages[idx] = asWritableRaster(dataBuffers.get(idx), width, height, parameters);
        }

        return rasterImages;
    }

    private static WritableRaster asWritableRaster(DataBuffer buffer, int width, int height, AutoContrastParameters parameters) {
        if (parameters != null) {
            AutoContrast.apply(buffer, parameters);
        }

        return Raster.createInterleavedRaster(buffer, width, height, width, 1, new int[]{0}, new Point(0, 0));
    }

    private static DataBufferUShort fromSignedShort(short[] data, int length, int offset) {
        for (int idx = offset; idx < offset + length; idx++) {
            data[idx] = (short) (data[idx] + 32768);
        }

        return new DataBufferUShort(data, length, offset);
    }
}
