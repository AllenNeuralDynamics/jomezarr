package org.aind.omezarr.image;

import org.aind.omezarr.OmeZarrAxis;
import org.aind.omezarr.OmeZarrDataset;
import ucar.ma2.InvalidRangeException;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.IOException;
import java.util.ArrayList;

public class OmeZarrImage {

    private OmeZarrDataset dataset;

    private boolean isUnsigned;

    private ColorModel colorModel;

    private int chunkShape[];

    private int chunkOffset[];

    private DataBuffer dataBuffer;

    public OmeZarrImage(OmeZarrDataset dataset, int timeIndex, int channelIndex, int zIndex) throws IOException {
        int[] shape = dataset.getRawShape();

        int[] offset = new int[shape.length];

        ArrayList<OmeZarrAxis> axes = dataset.getMultiscale().getAxes();

        for (int idx =0; idx < axes.size(); idx ++) {
            switch (axes.get(idx).getType()) {
                case TIME:
                    shape[idx] = 1;
                    offset[idx] = timeIndex;
                    break;
                case CHANNEL:
                    shape[idx] = 1;
                    offset[idx] = channelIndex;
                    break;
                case SPACE:
                    if (axes.get(idx).getName().equalsIgnoreCase("z")) {
                        shape[idx] = 1;
                        offset[idx] = zIndex;
                    }
            }
        }

        initialize(dataset, shape, offset);
    }

    public OmeZarrImage(OmeZarrDataset dataset, int[] chunkShape, int[] chunkOffset) throws IOException {
        initialize(dataset, chunkShape, chunkOffset);
    }

    private void initialize(OmeZarrDataset dataset, int[] chunkShape, int[] chunkOffset) throws IOException {
        this.dataset = dataset;

        this.chunkShape = chunkShape;

        this.chunkOffset = chunkOffset;

        colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), false, true, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);

        isUnsigned = dataset.getIsUnsigned();
    }

    public ColorModel getColorModel() {
        return colorModel;
    }

    public DataBuffer toDataBuffer() throws IOException, InvalidRangeException {
        if (dataBuffer == null) {
            short[] data = dataset.readShort(chunkShape, chunkOffset);

            if (!isUnsigned) {
                dataBuffer = fromSignedShort(data);
            } else {
                dataBuffer = new DataBufferUShort(data, data.length);
            }
        }

        return dataBuffer;
    }

    public WritableRaster asRaster() throws IOException, InvalidRangeException {
        return asRaster(false);
    }

    public WritableRaster asRaster(boolean autoContrast) throws IOException, InvalidRangeException {
        return asWritableRaster(autoContrast, null);
    }

    public WritableRaster asRaster(boolean autoContrast, AutoContrastParameters parameters) throws IOException, InvalidRangeException {
        return asWritableRaster(autoContrast, parameters);
    }

    public BufferedImage asImage(boolean autoContrast) throws IOException, InvalidRangeException {
        WritableRaster raster = asWritableRaster(autoContrast, null);

        return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
    }

    public BufferedImage asImage(boolean autoContrast, AutoContrastParameters parameters) throws IOException, InvalidRangeException {
        WritableRaster raster = asWritableRaster(autoContrast, parameters);

        return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
    }

    private WritableRaster asWritableRaster(boolean autoContrast, AutoContrastParameters parameters) throws IOException, InvalidRangeException {
        DataBuffer buffer = toDataBuffer();

        if (autoContrast) {
            if (parameters == null) {
                parameters = AutoContrastParameters.fromBuffer(buffer);
            }
            AutoContrast.apply(buffer, parameters);
        }

        return Raster.createInterleavedRaster(buffer, chunkShape[4], chunkShape[3], chunkShape[4], 1, new int[]{0}, new Point(0, 0));
    }

    private DataBufferUShort fromSignedShort(short[] data) {
        for (int idx = 0; idx < data.length; idx++) {
            data[idx] = (short) (data[idx] + 32768);
        }

        return new DataBufferUShort(data, data.length);
    }
}
