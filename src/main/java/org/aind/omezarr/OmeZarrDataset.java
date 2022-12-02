package org.aind.omezarr;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.bc.zarr.DataType;
import com.bc.zarr.ZarrArray;
import ucar.ma2.InvalidRangeException;

public class OmeZarrDataset {
    private String path;

    private OmeZarrMultiscale multiscale;

    private ArrayList<OmeZarrCoordinateTransformation> coordinateTransformations;

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
    }

    public int[] getShape() throws IOException {
        ZarrArray reopenedArray = ZarrArray.open(Paths.get(getMultiscale().getAttributes().getFileset().getPath().toString(), path));

        return reopenedArray.getShape();
    }

    public boolean getIsUnsigned() throws IOException {
        ZarrArray reopenedArray = ZarrArray.open(Paths.get(getMultiscale().getAttributes().getFileset().getPath().toString(), path));

        DataType datatype = reopenedArray.getDataType();

        return datatype == DataType.u1 || datatype == DataType.u2 || datatype == DataType.u4;
    }

    public short[] readShort() throws IOException, InvalidRangeException {
        ZarrArray reopenedArray = ZarrArray.open(Paths.get(getMultiscale().getAttributes().getFileset().getPath().toString(), path));

        int[] shape = reopenedArray.getShape();
        int[] fromPosition = new int[shape.length];

        if (shape.length != 2) {
            throw new InvalidRangeException();
        }

        return (short[]) reopenedArray.read(shape, fromPosition);
    }

    public short[] readShort(int timeIndex, int channelIndex, int zIndex) throws IOException, InvalidRangeException {
        ZarrArray reopenedArray = ZarrArray.open(Paths.get(getMultiscale().getAttributes().getFileset().getPath().toString(), path));

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
}
