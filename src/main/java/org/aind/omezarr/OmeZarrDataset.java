package org.aind.omezarr;

import com.bc.zarr.DataType;
import com.bc.zarr.ZarrArray;
import org.aind.omezarr.zarr.ExternalZarrStore;
import ucar.ma2.InvalidRangeException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class OmeZarrDataset {
    private String path;

    private OmeZarrMultiscale multiscale;

    private ArrayList<OmeZarrCoordinateTransformation> coordinateTransformations;

    private double minSpatialResolution;

    private ExternalZarrStore externalZarrStore;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ExternalZarrStore getExternalZarrStore() {
        return externalZarrStore;
    }

    public void setExternalZarrStore(ExternalZarrStore externalZarrStore) {
        this.externalZarrStore = externalZarrStore;
    }

    public ArrayList<OmeZarrCoordinateTransformation> getCoordinateTransformations() {
        return coordinateTransformations;
    }

    public void setCoordinateTransformations(ArrayList<OmeZarrCoordinateTransformation> transformations) {
        coordinateTransformations = transformations;
    }

    public OmeZarrMultiscale getMultiscale() {
        return multiscale;
    }

    public void setMultiscale(OmeZarrMultiscale multiscale) {
        this.multiscale = multiscale;

        minSpatialResolution = multiscale.getSpatialIndices().stream().reduce(0.0,
                (min, idx) -> {
                    for (OmeZarrCoordinateTransformation transform : coordinateTransformations) {
                        if (transform.getType() == OmeZarrCoordinateTransformationType.SCALE) {
                            if (min == 0 || transform.getScale()[idx] < min) {
                                min = transform.getScale()[idx];
                            }
                        }
                    }

                    return min;
                },
                Double::sum);
    }

    public Path getParentPath() {
        return getMultiscale().getAttributes().getOmeZarrGroup().getRootPath();
    }

    public Path getFullPath() {
        Path root = getParentPath();

        if (root != null) {
            return root.resolve(path);
        } else {
            return Paths.get(path);
        }
    }

    public int[] getShape() throws IOException {
        ZarrArray array = open();

        return array.getShape();
    }

    public List<Double> getSpatialResolution(OmeZarrAxisUnit unitType) {
        if (unitType != OmeZarrAxisUnit.MICROMETER) {
            throw new IllegalArgumentException();
        }

        return multiscale.getSpatialIndices().stream().map(idx -> {
            double scale = 1.0;

            for (OmeZarrCoordinateTransformation transform : coordinateTransformations) {
                if (transform.getType() == OmeZarrCoordinateTransformationType.SCALE) {
                    scale *= transform.getScale()[idx];
                }
            }

            return scale;
        }).collect(Collectors.toList());
    }

    public boolean getIsUnsigned() throws IOException {
        ZarrArray array = open();

        DataType datatype = array.getDataType();

        return datatype == DataType.u1 || datatype == DataType.u2 || datatype == DataType.u4;
    }

    public double getMinSpatialResolution() {
        return minSpatialResolution;
    }

    public boolean isValid() {
        try {
            int[] shape = getShape();

            return shape.length > 0;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * Reads an entire 2-D array.
     *
     * @return
     * @throws IOException
     * @throws InvalidRangeException
     */
    public short[] readShort() throws IOException, InvalidRangeException {
        ZarrArray array = open();

        int[] shape = array.getShape();
        int[] fromPosition = new int[shape.length];

        if (shape.length != 2) {
            throw new InvalidRangeException();
        }

        return (short[]) array.read(shape, fromPosition);
    }

    /**
     * Reads an individual 2-D x-y slice of a 5-D array.
     *
     * @param timeIndex    time slice
     * @param channelIndex channel slice
     * @param zIndex       z slice
     * @return
     * @throws IOException
     * @throws InvalidRangeException
     */
    public short[] readShort(int timeIndex, int channelIndex, int zIndex) throws IOException, InvalidRangeException {
        ZarrArray array = open();

        int[] shape = array.getShape();

        if (shape.length != 5) {
            throw new InvalidRangeException();
        }

        shape[0] = 1;
        shape[1] = 1;
        shape[2] = 1;

        int[] fromPosition = {timeIndex, channelIndex, zIndex, 0, 0};

        return (short[]) array.read(shape, fromPosition);
    }

    public byte[] readByte(int[] shape, int[] fromPosition) throws IOException, InvalidRangeException {
        ZarrArray array = open();

        return (byte[]) array.read(shape, fromPosition);
    }

    /**
     * Reads an arbitrary chunk of the array.
     *
     * @param shape
     * @param fromPosition
     * @return
     * @throws IOException
     * @throws InvalidRangeException
     */
    public short[] readShort(int[] shape, int[] fromPosition) throws IOException, InvalidRangeException {

        // This is primarily to mask the underlying use of JZarr versus any other implementation.
        ZarrArray array = open();

        return (short[]) array.read(shape, fromPosition);
    }

    public short[] readShortAsParallel(int[] shape, int[] fromPosition, int numTasks) throws IOException {
        class ReadEntry {
            public int index;
            public int[] shape;
            public int[] offset;

            public ReadEntry(int index, int[] shape, int[] offset) {
                this.index = index;
                this.shape = shape;
                this.offset = offset;
            }
        }
        List<ReadEntry> readEntries = new ArrayList<>();

        readEntries.add(new ReadEntry(0, shape, fromPosition));

        int zIndex = shape.length - 3;

        if (shape.length > 2 && shape[2] > 1) {
            for (int idx = 0; idx < shape.length - 2; idx++) {
                if (shape[idx] != 1) {
                    break;
                }
            }

            readEntries.clear();

            int interval = shape[zIndex] / numTasks;

            int index = 0;

            for (int intervalCount = fromPosition[zIndex]; intervalCount < shape[zIndex]; intervalCount += interval) {
                int[] readOffset = new int[fromPosition.length];
                System.arraycopy(fromPosition, 0, readOffset, 0, shape.length);
                readOffset[zIndex] = intervalCount;

                int[] readShape = new int[shape.length];
                System.arraycopy(shape, 0, readShape, 0, shape.length);
                readShape[zIndex] = Math.min(interval, shape[zIndex] - intervalCount);

                readEntries.add(new ReadEntry(index, readShape, readOffset));

                index++;
            }
        }

        Map<ReadEntry, short[]> data = new ConcurrentHashMap<>();

        int bufferSize = shape[shape.length - 1] * shape[shape.length - 2];

        int size = bufferSize * shape[shape.length - 3];

        short[] result = new short[size];

        ZarrArray array = open();

        readEntries.parallelStream().forEach(entry -> {
            try {
                data.put(entry, (short[]) array.read(entry.shape, entry.offset));
            } catch (Exception ex) {
                data.put(entry, null);
            }
        });

        int offset = 0;

        for (ReadEntry entry : readEntries) {
            short[] buffer = data.get(entry);

            if (buffer == null) {
                throw new IOException();
            }

            System.arraycopy(buffer, 0, result, offset, buffer.length);

            offset += buffer.length;
        }

        return result;
    }

    private ZarrArray open() throws IOException {
        if (externalZarrStore != null) {
            return ZarrArray.open(externalZarrStore);
        }

        return ZarrArray.open(getFullPath());
    }
}
