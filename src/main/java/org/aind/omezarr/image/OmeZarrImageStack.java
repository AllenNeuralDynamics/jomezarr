package org.aind.omezarr.image;

import org.aind.omezarr.OmeZarrDataset;
import ucar.ma2.InvalidRangeException;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.IOException;

public class OmeZarrImageStack {

    private OmeZarrDataset dataset;

    private ColorModel colorModel;

    private boolean isUnsigned;

    public OmeZarrImageStack(OmeZarrDataset dataset) throws IOException {
        this.dataset = dataset;

        colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), false, true, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);

        isUnsigned = dataset.getIsUnsigned();
    }

    public ColorModel getColorModel() {
        return colorModel;
    }

    public Raster[] asSlices(int time, int channel) throws IOException, InvalidRangeException {
        return asSlices(time, channel, 0, Integer.MAX_VALUE, false);
    }

    public WritableRaster[] asSlices(int time, int channel, boolean autoContrast) throws IOException, InvalidRangeException {
        return asSlices(time, channel, 0, Integer.MAX_VALUE, autoContrast);
    }

    public WritableRaster[] asSlices(int time, int channel, int limit) throws IOException, InvalidRangeException {
        return asSlices(time, channel, 0, limit, false);
    }

    public WritableRaster[] asSlices(int time, int channel, int offset, int count, boolean autoContrast) throws IOException, InvalidRangeException {
        int[] shape = dataset.getRawShape();

        if (offset > shape[2]) {
            offset = shape[2] - 1;
        }

        int last = offset + count;

        if (shape[2] < last) {
            last = shape[2];
            count = last - offset;
        }

        WritableRaster[] rasterImages = new WritableRaster[count];

        AutoContrastParameters parameters = null;

        if (autoContrast) {
            int idx = (int) (offset + count / 2.0);

            OmeZarrImage image = new OmeZarrImage(dataset, time, channel, idx);

            parameters = AutoContrastParameters.fromBuffer(image.toDataBuffer());
        }

        for (int idx = 0; idx < rasterImages.length; idx++) {
            OmeZarrImage image = new OmeZarrImage(dataset, time, channel, offset + idx);
            rasterImages[idx] = image.asRaster(autoContrast, parameters);
        }
        return rasterImages;
    }

    public WritableRaster[] asSlices(int[] chunkShape, int[] chunkOffset, boolean autoContrast) throws IOException, InvalidRangeException {
        int count = chunkShape[2];

        WritableRaster[] rasterImages = new WritableRaster[count];

        AutoContrastParameters parameters = null;

        int[] currentOffset = new int[chunkOffset.length];

        currentOffset[0] = chunkOffset[0];
        currentOffset[1] = chunkOffset[1];
        currentOffset[2] = chunkOffset[2];
        currentOffset[3] = chunkOffset[3];
        currentOffset[4] = chunkOffset[4];

        if (autoContrast) {
            int idx = (int) (count / 2.0);

            currentOffset[2] = idx;

            OmeZarrImage image = new OmeZarrImage(dataset, chunkShape, chunkOffset);

            parameters = AutoContrastParameters.fromBuffer(image.toDataBuffer());
        }

        for (int idx = 0; idx < rasterImages.length; idx++) {
            currentOffset[2] = idx;
            OmeZarrImage image = new OmeZarrImage(dataset, chunkShape, currentOffset);
            rasterImages[idx] = image.asRaster(autoContrast, parameters);
        }
        return rasterImages;
    }
}
