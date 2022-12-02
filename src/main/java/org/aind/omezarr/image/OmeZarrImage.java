package org.aind.omezarr.image;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.IOException;

import org.aind.omezarr.*;
import ucar.ma2.InvalidRangeException;

public class OmeZarrImage {

    private OmeZarrDataset dataset;

    private boolean isUnsigned;

    private ColorModel colorModel;

    private int timeIndex = -1;

    private int channelIndex = -1;

    private int zIndex = -1;

    private DataBuffer dataBuffer;

    public OmeZarrImage(OmeZarrDataset dataset) throws IOException {
        this(dataset, -1, -1, -1);
    }

    public OmeZarrImage(OmeZarrDataset dataset, int timeIndex, int channelIndex, int zIndex) throws IOException {
        this.dataset = dataset;

        this.timeIndex = timeIndex;

        this.channelIndex = channelIndex;

        this.zIndex = zIndex;

        colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), false, true, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);

        isUnsigned = dataset.getIsUnsigned();
    }

    public ColorModel getColorModel() {
        return colorModel;
    }

    public int getTimeIndex() {
        return timeIndex;
    }

    public int getChannelIndex() {
        return channelIndex;
    }

    public int getzIndex() {
        return zIndex;
    }

    public DataBuffer toDataBuffer() throws IOException, InvalidRangeException {
        if (dataBuffer == null) {
            short data[];

            if (timeIndex == -1 || channelIndex == -1 || zIndex == -1) {
                data = dataset.readShort();
            } else {
                data = dataset.readShort(timeIndex, channelIndex, zIndex);
            }

            if (!isUnsigned) {
                dataBuffer = fromSignedShort(data);
            } else {
                dataBuffer = new DataBufferUShort(data, data.length);
            }
        }

        return dataBuffer;
    }

    public Raster asRaster() throws IOException, InvalidRangeException {
        return asRaster(false);
    }

    public Raster asRaster(boolean autoContrast) throws IOException, InvalidRangeException {
        return asWritableRaster(autoContrast);
    }

    public BufferedImage asImage(boolean autoContrast) throws IOException, InvalidRangeException {
        WritableRaster raster = asWritableRaster(autoContrast);

        return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
    }

    private WritableRaster asWritableRaster(boolean autoContrast) throws IOException, InvalidRangeException {
        int[] shape = dataset.getShape();

        DataBuffer buffer = toDataBuffer();

        if (autoContrast) {
            AutoContrast.apply(buffer);
        }

        return Raster.createInterleavedRaster(buffer, shape[4], shape[3], shape[4], 1, new int[]{0}, new Point(0, 0));
    }

    private DataBufferUShort fromSignedShort(short[] data) {
        for (int idx = 0; idx < data.length; idx++) {
            data[idx] = (short) (data[idx] + 32768);
        }

        return new DataBufferUShort(data, data.length);
    }
}
