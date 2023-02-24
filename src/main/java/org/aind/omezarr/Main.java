package org.aind.omezarr;

import com.bc.zarr.*;
import org.aind.omezarr.image.AutoContrast;
import org.aind.omezarr.image.OmeZarrImage;
import ucar.ma2.InvalidRangeException;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Quick and dirty prototyping w/o setting up a test.
 */
public class Main {
    private static Path YX_SAMPLE_FILESET;

    private static Path TCZYX_SAMPLE_FILESET;

    private static final String YX_SAMPLE_NAME = "yx.ome.zarr";

    private static final String TCZYX_SAMPLE_NAME = "tczyx.ome.zarr";

    public static void main(String[] args) throws IOException, InvalidRangeException {
        YX_SAMPLE_FILESET = Paths.get(System.getProperty("TestSampleDir"), YX_SAMPLE_NAME);

        TCZYX_SAMPLE_FILESET = Paths.get(System.getProperty("TestSampleDir"), TCZYX_SAMPLE_NAME);

        readExample();

        //readImageExample();

        // writeExample();

        // convertExample();
    }

    private static void readExample() throws IOException, InvalidRangeException {
        Path path = YX_SAMPLE_FILESET;

        OmeZarrGroup root = OmeZarrGroup.open(path);

        OmeZarrDataset dataset = root.getAttributes().getMultiscales()[0].getDatasets().get(0);

        short[] data = dataset.readShort();

        java.util.List<Short> sh = IntStream.range(0, data.length).mapToObj(s -> data[s]).collect(Collectors.toList());

        short min = Collections.min(sh);
        short max = Collections.max(sh);

        System.out.printf("min: %d, max: %d%n", min, max);
    }

    private static void readImageExample() throws IOException, InvalidRangeException {
        Path path = TCZYX_SAMPLE_FILESET;

        OmeZarrGroup fileset = OmeZarrGroup.open(path);

        OmeZarrDataset dataset = fileset.getAttributes().getMultiscales()[0].getDatasets().get(0);

        OmeZarrImage image = new OmeZarrImage(dataset, 0, 0, 0);

        DataBuffer buffer = image.toDataBuffer();

        AutoContrast.apply(buffer);

        System.out.printf(String.valueOf(buffer.getSize()));
    }

    private static void writeExample() throws IOException, InvalidRangeException {
        Path path = Paths.get(System.getProperty("TestSampleDir"), "jomezarr.zarr");

        ZarrArray createdArray = ZarrArray.create(path, new ArrayParams()
                .shape(1, 1, 2, 3, 4)
                .chunks(1, 1, 2, 3, 4)
                .dataType(DataType.u2)
                .fillValue(0)
        );

        int[] value = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24};
        int[] withShape = {1, 1, 2, 3, 4};
        int[] toPosition = {0, 0, 0, 0, 0};

        createdArray.write(value, withShape, toPosition);

        ZarrArray reopenedArray = ZarrArray.open(path);

        int[] shape = {1, 1, 2, 2, 2}; // reopenedArray.getShape();
        int[] fromPosition = {0, 0, 0, 0, 0};
        short[] values = (short[])reopenedArray.read(shape, fromPosition);

        for (short val : values) {
            System.out.printf("%d\n", val);
        }

        /*
        short[] shorts = {-30000, -1, 0, 1, 30000};

        DataBufferUShort buffer = new DataBufferUShort(shorts, shorts.length);

        for (int idx = 0; idx < buffer.getSize(); idx++) {
            System.out.printf("(%d): %d\n", idx, buffer.getElem(idx));
        }

        short[] loadedData = buffer.getData();

        for (int idx = 0; idx < loadedData.length; idx++) {
            System.out.printf("(%d): %d\n", idx, loadedData[idx]);
        }
         */
    }

    private static void convertExample() throws IOException, InvalidRangeException {
        for (int idx = 14; idx > 4; idx--) {
            ZarrArray reopenedArray = ZarrArray.open(String.format("E:\\aind\\samples\\2022-11-22-fused.ome.zarr\\%d", idx));

            int[] shape = reopenedArray.getShape();
            int[] chunksize = {1, 1, 251, 1536, 1024};
            int[] fromPosition = {0, 0, 0, 0, 0};

            short[] data = (short[]) reopenedArray.read(shape, fromPosition);

            Compressor compNull = CompressorFactory.create("null");

            ZarrArray createdArray = ZarrArray.create(String.format("E:\\aind\\rewrite\\2022-11-22-fused.ome.zarr\\%d", idx), new ArrayParams()
                    .shape(shape)
                    .chunks(chunksize)
                    .dataType(DataType.u2)
                    .fillValue(0)
                    .compressor(compNull)
            );

            createdArray.write(data, shape, fromPosition);
        }
    }
}
