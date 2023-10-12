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

    private OmeZarrIndexGenerator indexGenerator;

    private OmeZarrIndex sizeIndex = null;

    private OmeZarrIndex chunkSizeIndex = null;

    private OmeZarrValue voxelSize = null;

    private OmeZarrPoint origin = null;

    private OmeZarrPoint extents = null;

    private Boolean isValid = null;

    private Boolean isUnsigned = null;

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

    public OmeZarrIndexGenerator getIndexGenerator() {
        return indexGenerator;
    }

    public void setIndexGenerator(OmeZarrIndexGenerator indexGenerator) {
        this.indexGenerator = indexGenerator;
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

    public OmeZarrIndex getShapeIndex() {
        if (sizeIndex == null) {
            sizeIndex = indexGenerator.createIndex(getRawShape());
        }

        return sizeIndex;
    }

    public OmeZarrIndex getChunksIndex() {
        if (chunkSizeIndex == null) {
            chunkSizeIndex = indexGenerator.createIndex(getRawChunks());
        }

        return chunkSizeIndex;
    }

    public int[] getRawShape() {
        try {
            ZarrArray array = open();

            return array.getShape();
        } catch (IOException e) {
            return null;
        }
    }

    public int[] getRawChunks() {
        try {
            ZarrArray array = open();

            return array.getChunks();
        } catch (IOException e) {
            return null;
        }
    }

    public OmeZarrPoint getSpatialOrigin(OmeZarrAxisUnit unitType) {
        if (origin != null) {
            return origin;
        }

        if (unitType != OmeZarrAxisUnit.MICROMETER) {
            throw new IllegalArgumentException("Micrometer is the only supported unit");
        }

        // TODO apply any translation in .zattrs
        origin = new OmeZarrPoint(0, 0, 0);

        return origin;
    }

    public OmeZarrPoint getSpatialExtents(OmeZarrAxisUnit unitType) {
        if (extents != null) {
            return extents;
        }
        OmeZarrValue voxelSize = getSpatialResolution(unitType);

        OmeZarrIndex shape = getShapeIndex();

        if (voxelSize.getZ() != OmeZarrValue.InvalidPosition && shape.getZ() != OmeZarrIndex.InvalidPosition) {
            extents = new OmeZarrPoint(shape.getX() * voxelSize.getX(), shape.getY() * voxelSize.getY(), shape.getZ() * voxelSize.getZ());
        } else {
            extents = new OmeZarrPoint(shape.getX() * voxelSize.getX(), shape.getY() * voxelSize.getY(), 0.0);
        }
        return extents;
    }

    /**
     * Returns the spatial resolution with any scale transform applied in z, y, x order
     *
     * @param unitType
     * @return
     */
    public OmeZarrValue getSpatialResolution(OmeZarrAxisUnit unitType) {
        if (unitType != OmeZarrAxisUnit.MICROMETER) {
            throw new IllegalArgumentException("Micrometer is the only supported unit");
        }

        if (voxelSize != null) {
            return voxelSize;
        }

        List<Double> values = multiscale.getSpatialIndices().stream().map(idx -> {
            double scale = 1.0;

            for (OmeZarrCoordinateTransformation transform : coordinateTransformations) {
                if (transform.getType() == OmeZarrCoordinateTransformationType.SCALE) {
                    scale *= transform.getScale()[idx];
                }
            }

            return scale;
        }).collect(Collectors.toList());

        if (values.size() < 2) {
            voxelSize = OmeZarrValue.InvalidValue;
        } else if (values.size() < 3) {
            voxelSize = new OmeZarrValue(values.get(0), values.get(1));
        } else {
            voxelSize = new OmeZarrValue(values.get(0), values.get(1), values.get(2));
        }

        return voxelSize;
    }

    public OmeZarrReadChunk readChunkForLocation(OmeZarrValue location) {
        OmeZarrIndex chunksIndex = getChunksIndex();
        OmeZarrValue voxelSize = getSpatialResolution(OmeZarrAxisUnit.MICROMETER);

        OmeZarrIndex shapeIndex = getShapeIndex();

        int x = (int) Math.floor(location.getX() / voxelSize.getX() / chunksIndex.getX()) * chunksIndex.getX();
        int y = (int) Math.floor(location.getY() / voxelSize.getY() / chunksIndex.getY()) * chunksIndex.getY();
        int z = (int) Math.floor(location.getZ() / voxelSize.getZ() / chunksIndex.getZ()) * chunksIndex.getZ();

        if (x < 0 || y < 0 || z < 0) {
            return null;
        }

        if (x > shapeIndex.getX() || y > shapeIndex.getY() || z > shapeIndex.getZ()) {
            return null;
        }

        int length = chunksIndex.get().length;

        int[] shape = new int[length];
        int[] offset = new int[length];

        int nextIndex = 0;

        if (chunksIndex.getT() != OmeZarrValue.InvalidPosition) {
            shape[nextIndex] = 1;
            offset[nextIndex] = (int) location.getT();
            nextIndex++;
        }

        if (chunksIndex.getC() != OmeZarrValue.InvalidPosition) {
            shape[nextIndex] = 1;
            offset[nextIndex] = (int) location.getC();
            nextIndex++;
        }

        if (chunksIndex.getZ() != OmeZarrValue.InvalidPosition) {
            offset[nextIndex] = z;
            shape[nextIndex] = Math.min(chunksIndex.getZ(), shapeIndex.getZ() - z);
            if (shape[nextIndex] == 0) {
                return null;
            }
            nextIndex++;
        }

        offset[nextIndex] = y;
        shape[nextIndex] = Math.min(chunksIndex.getY(), shapeIndex.getY() - y);
        if (shape[nextIndex] == 0) {
            return null;
        }
        nextIndex++;

        offset[nextIndex] = x;
        shape[nextIndex] = Math.min(chunksIndex.getX(), shapeIndex.getX() - x);
        if (shape[nextIndex] == 0) {
            return null;
        }

        return new OmeZarrReadChunk(shape, offset);
    }

    public boolean getIsUnsigned() throws IOException {
        if (isUnsigned == null) {
            ZarrArray array = open();

            DataType datatype = array.getDataType();

            isUnsigned = datatype == DataType.u1 || datatype == DataType.u2 || datatype == DataType.u4;
        }

        return isUnsigned;
    }

    public double getMinSpatialResolution() {
        return minSpatialResolution;
    }

    public boolean isValid() {
        if (isValid == null) {
            int[] shape = getRawShape();

            isValid = shape.length > 0;
        }

        return isValid;
    }

    /**
     * Reads an entire 2-D array.
     *
     * @return array of short values
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
     * @return array of short values
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
     * @return array of short values
     */
    public short[] readShort(int[] shape, int[] fromPosition) throws IOException, InvalidRangeException {
        ZarrArray array = open();

        return (short[]) array.read(shape, fromPosition);
    }

    public short[] readShortAsParallel(int[] shape, int[] fromPosition, int numTasks) throws IOException {
        class ReadEntry {
            public final int index;
            public final int[] shape;
            public final int[] offset;

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
