package org.aind.omezarr;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.bc.zarr.ArrayParams;
import com.bc.zarr.DataType;
import com.bc.zarr.ZarrArray;
import org.aind.omezarr.image.AutoContrast;
import org.aind.omezarr.image.OmeZarrImage;
import ucar.ma2.InvalidRangeException;

/**
 * Quick and dirty prototyping w/o setting up a test.
 */
public class Main {
    private static Path YX_SAMPLE_FILESET;

    private static Path TCZYX_SAMPLE_FILESET;

    private static final String YX_SAMPLE_NAME = "yx.ome.zarr";

    private static final  String TCZYX_SAMPLE_NAME = "tczyx.ome.zarr";

    public static void main(String[] args) throws IOException, InvalidRangeException {
        YX_SAMPLE_FILESET = Paths.get(System.getProperty("TestSampleDir"), YX_SAMPLE_NAME );

        TCZYX_SAMPLE_FILESET = Paths.get(System.getProperty("TestSampleDir"), TCZYX_SAMPLE_NAME );

        readExample();

        readImageExample();

        writeExample();
    }

    private static void readExample() throws IOException, InvalidRangeException {
        Path path = YX_SAMPLE_FILESET;

        OmeZarrGroup fileset = OmeZarrGroup.open(path);

        OmeZarrDataset dataset = fileset.getAttributes().getMultiscales()[0].getDatasets()[0];

        short[] data = dataset.readShort();

        java.util.List<Short> sh = IntStream.range(0, data.length).mapToObj(s -> data[s]).collect(Collectors.toList());

        short min = Collections.min(sh);
        short max = Collections.max(sh);

        System.out.printf("min: %d, max: %d%n", min, max);
    }

    private static void readImageExample() throws IOException, InvalidRangeException {
        Path path = TCZYX_SAMPLE_FILESET;

        OmeZarrGroup fileset = OmeZarrGroup.open(path);

        OmeZarrDataset dataset = fileset.getAttributes().getMultiscales()[0].getDatasets()[0];

        OmeZarrImage image = new OmeZarrImage(dataset, 0, 0, 0);

        DataBuffer buffer = image.toDataBuffer();

        AutoContrast.apply(buffer);

        System.out.printf(String.valueOf(buffer.getSize()));
    }

    private static void writeExample() throws IOException, InvalidRangeException {
        Path path = Paths.get(System.getProperty("TestSampleDir"), "jomezarr.zarr");

        ZarrArray createdArray = ZarrArray.create(path, new ArrayParams()
                .shape(3, 3)
                .chunks(3, 3)
                .dataType(DataType.u2)
                .fillValue(50000)
        );

        int value = 2;
        int[] withShape = {1, 1};
        int[] toPosition = {1, 1};

        createdArray.write(value, withShape, toPosition);

        ZarrArray reopenedArray = ZarrArray.open(path);

        int[] shape = reopenedArray.getShape();
        int[] fromPosition = {0, 0};
        reopenedArray.read(shape, fromPosition);

        short[] shorts = {-30000, -1, 0, 1, 30000};

        DataBufferUShort buffer = new DataBufferUShort(shorts, shorts.length);

        for (int idx = 0; idx < buffer.getSize(); idx++) {
            System.out.printf("(%d): %d\n", idx, buffer.getElem(idx));
        }

        short[] loadedData = buffer.getData();

        for (int idx = 0; idx < loadedData.length; idx++) {
            System.out.printf("(%d): %d\n", idx, loadedData[idx]);
        }
    }
}
