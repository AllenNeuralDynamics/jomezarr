package org.aind.omezarr.image;

import org.aind.omezarr.OmeZarrDataset;

import java.awt.*;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

public class TCZYXRasterZStack {

    public static WritableRaster[] fromDataset(OmeZarrDataset dataset, int[] shape, int[] offset, boolean autoContrast) {
        if (shape.length < 5) {
            throw new IllegalArgumentException("shape");
        }

        if (offset.length < 5) {
            throw new IllegalArgumentException("offset");
        }

        if (!dataset.isValid()) {
            throw new IllegalArgumentException("dataset");
        }

        int count = shape[2];

        WritableRaster[] rasterImages = new WritableRaster[count];

        try {
            short[] data = dataset.readShort(shape, offset);

            int zPlaneLength = shape[3] * shape[4];

            ArrayList<DataBufferUShort> dataBuffers = new ArrayList<>();

            for (int zIdx = 0; zIdx < count; zIdx++) {
                if (!dataset.getIsUnsigned()) {
                    dataBuffers.add(fromSignedShort(data, zPlaneLength, zIdx * zPlaneLength));
                } else {
                    dataBuffers.add(new DataBufferUShort(data, zPlaneLength, zIdx * zPlaneLength));
                }
            }

            AutoContrastParameters parameters = null;

            if (autoContrast) {
                int idx = (int) (count / 2.0);

                parameters = AutoContrastParameters.fromBuffer(dataBuffers.get(idx));
            }

            for (int idx = 0; idx < rasterImages.length; idx++) {
                rasterImages[idx] = asWritableRaster(dataBuffers.get(idx), shape[4], shape[3], autoContrast, parameters);
            }
        } catch (Exception ex) {
            return null;
        }

        return rasterImages;
    }

    private static WritableRaster asWritableRaster(DataBuffer buffer, int width, int height, boolean autoContrast, AutoContrastParameters parameters) {
        if (autoContrast) {
            if (parameters == null) {
                parameters = AutoContrastParameters.fromBuffer(buffer);
            }

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
