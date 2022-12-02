package org.aind.omezarr.image;

import org.aind.omezarr.OmeZarrDataset;
import ucar.ma2.InvalidRangeException;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
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
        return asSlices(time, channel, Integer.MAX_VALUE, false);
    }

    public Raster[] asSlices(int time, int channel, boolean autoContrast) throws IOException, InvalidRangeException {
        return asSlices(time, channel,  Integer.MAX_VALUE, autoContrast);
    }

    public Raster[] asSlices(int time, int channel, int limit) throws IOException, InvalidRangeException {
        return asSlices(time, channel, limit, false);
    }

    public Raster[] asSlices(int time, int channel, int limit, boolean autoContrast) throws IOException, InvalidRangeException {
        int[] shape = dataset.getShape();

        if (shape[2] < limit) {
            limit = shape[2];
        }

        Raster[] rasterImages = new Raster[limit];

        for (int idx = 0; idx < rasterImages.length; idx++) {
            OmeZarrImage image = new OmeZarrImage(dataset, time, channel, idx);
            rasterImages[idx] = image.asRaster(autoContrast);
        }
        return rasterImages;
    }
}
