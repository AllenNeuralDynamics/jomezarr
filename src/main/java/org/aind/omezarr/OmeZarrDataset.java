package org.aind.omezarr;

import com.bc.zarr.DataType;
import com.bc.zarr.ZarrArray;
import ucar.ma2.InvalidRangeException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OmeZarrDataset {
    private String path;

    private OmeZarrMultiscale multiscale;

    private ArrayList<OmeZarrCoordinateTransformation> coordinateTransformations;

    private double minSpatialResolution;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public Path getFullPath() {
        Path root = getMultiscale().getAttributes().getFileset().getPath();

        if (root != null) {
            return root.resolve(path);
        } else {
            return Paths.get(path);
        }
    }

    public int[] getShape() throws IOException {
        ZarrArray reopenedArray = ZarrArray.open(getFullPath());

        return reopenedArray.getShape();
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
        ZarrArray reopenedArray = ZarrArray.open(getFullPath());

        DataType datatype = reopenedArray.getDataType();

        return datatype == DataType.u1 || datatype == DataType.u2 || datatype == DataType.u4;
    }

    public double getMinSpatialResolution() {
        return minSpatialResolution;
    }

    public boolean isValid() {
        return getFullPath().toFile().exists();
    }

    /**
     * Reads an entire 2-D array.
     * @return
     * @throws IOException
     * @throws InvalidRangeException
     */
    public short[] readShort() throws IOException, InvalidRangeException {
        ZarrArray reopenedArray = ZarrArray.open(getFullPath());

        int[] shape = reopenedArray.getShape();
        int[] fromPosition = new int[shape.length];

        if (shape.length != 2) {
            throw new InvalidRangeException();
        }

        return (short[]) reopenedArray.read(shape, fromPosition);
    }

    /**
     * Reads an individual 2-D x-y slice of a 5-D array.
     * @param timeIndex time slice
     * @param channelIndex channel slice
     * @param zIndex z slice
     * @return
     * @throws IOException
     * @throws InvalidRangeException
     */
    public short[] readShort(int timeIndex, int channelIndex, int zIndex) throws IOException, InvalidRangeException {
        ZarrArray reopenedArray = ZarrArray.open(getFullPath());

        int[] shape = reopenedArray.getShape();

        if (shape.length != 5) {
            throw new InvalidRangeException();
        }

        shape[0] = 1;
        shape[1] = 1;
        shape[2] = 1;

        int[] fromPosition = {timeIndex, channelIndex, zIndex, 0, 0};

        return (short[]) reopenedArray.read(shape, fromPosition);
    }

    /**
     * Reads an arbitrary chunk of the array.
     * @param shape
     * @param fromPosition
     * @return
     * @throws IOException
     * @throws InvalidRangeException
     */
    public short[] readShort(int[] shape, int[] fromPosition) throws IOException, InvalidRangeException {
        // This is primarily to mask the underlying use of JZarr versus any other implementation.
        ZarrArray reopenedArray = ZarrArray.open(getFullPath());

        return (short[]) reopenedArray.read(shape, fromPosition);
    }
}
