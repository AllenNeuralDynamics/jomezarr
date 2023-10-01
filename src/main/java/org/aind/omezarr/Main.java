package org.aind.omezarr;

import com.bc.zarr.*;
import org.aind.omezarr.image.AutoContrast;
import org.aind.omezarr.image.OmeZarrImage;
import org.aind.omezarr.image.TCZYXRasterZStack;
import org.aind.omezarr.util.PerformanceMetrics;
import ucar.ma2.InvalidRangeException;

import java.awt.image.DataBuffer;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.aind.omezarr.zarr.ZarrProperties.setBloscCompressorThreads;

/**
 * Quick and dirty prototyping w/o setting up a test.
 */
public class Main {
    private static Path YX_SAMPLE_FILESET;

    private static Path TCZYX_SAMPLE_FILESET;

    private static Path AIND_SAMPLE_DATASET;

    private static Path AIND_SAMPLE_OUTPUT;

    private static final String YX_SAMPLE_NAME = "yx.ome.zarr";

    private static final String TCZYX_SAMPLE_NAME = "tczyx.ome.zarr";

    public static void main(String[] args) throws IOException, InvalidRangeException {
        YX_SAMPLE_FILESET = Paths.get(System.getProperty("TestSampleDir"), YX_SAMPLE_NAME);

        TCZYX_SAMPLE_FILESET = Paths.get(System.getProperty("TestSampleDir"), TCZYX_SAMPLE_NAME);

        AIND_SAMPLE_DATASET = Paths.get(System.getProperty("AindSampleDir"));

        AIND_SAMPLE_OUTPUT = Paths.get(System.getProperty("AindOutputDir"));

        // readExample();

        // readImageExample();

        // writeExample();

        // convertExample();

        // tczyxStackExample();

        // testCompressorPerf();

        testChunkForLocation();
    }

    private static void readExample() throws IOException, InvalidRangeException {
        Path path = YX_SAMPLE_FILESET;

        OmeZarrGroup root = OmeZarrGroup.open(path);

        OmeZarrDataset dataset = root.getAttributes().getMultiscales()[0].getDatasets().get(0);

        Instant start = Instant.now();

        short[] data = dataset.readShort();

        Duration total = Duration.between(start, Instant.now());

        System.out.printf("total duration: %s\n", total.toString());

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

    private static void tczyxStackExample() throws IOException {
        Path path = AIND_SAMPLE_DATASET;

        OmeZarrGroup fileset = OmeZarrGroup.open(path);

        OmeZarrDataset dataset = fileset.getAttributes().getMultiscales()[0].getDatasets().get(7);

        int[] shape = dataset.getRawShape();

        shape[0] = shape[1] = 1;

        System.out.printf("shape: [%d, %d, %d, %d, %d]\n", shape[0], shape[1], shape[2], shape[3], shape[4]);

        int[] offset = {0, 0, 0, 0, 0};

        PerformanceMetrics metrics = new PerformanceMetrics();

        Instant start = Instant.now();

        TCZYXRasterZStack.fromDataset(dataset, shape, offset, 1, true, null, metrics);

        Duration total = Duration.between(start, Instant.now());

        System.out.printf("total: %s\n", total.toString());
        System.out.printf("readDuration: %s\n", metrics.readDuration.toString());
        System.out.printf("dataBufferDuration: %s\n", metrics.dataBufferDuration.toString());
        System.out.printf("autoConstrastDuration: %s\n", metrics.autoConstrastDuration.toString());
        System.out.printf("rasterizeDuration: %s\n", metrics.rasterizeDuration.toString());
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
        short[] values = (short[]) reopenedArray.read(shape, fromPosition);

        for (short val : values) {
            System.out.printf("%d\n", val);
        }
    }

    private static void convertExample() throws IOException, InvalidRangeException {
        int maxX = 408;
        int maxY = 175;
        int maxZ = 213;

        boolean useCompressor = true;

        int[] offset = {0, 0, 0, 0, 0};

        for (int idx = 5; idx > 4; idx--) {
            ZarrArray reopenedArray = ZarrArray.open(AIND_SAMPLE_DATASET.resolve(String.format("%d", idx)));

            int[] shape = reopenedArray.getShape();

            int[] chunkSize = {1, 1, Math.min(shape[2], maxZ), Math.min(shape[3], maxY), Math.min(shape[4], maxX)};

            short[] data = (short[]) reopenedArray.read(shape, offset);

            Compressor compressor = useCompressor ?
                    CompressorFactory.create("blosc", "cname", "zstd", "clevel", 1, "nthreads", 4) :
                    CompressorFactory.create("null");

            ZarrArray createdArray = ZarrArray.create(AIND_SAMPLE_OUTPUT.resolve(String.format("%d", idx)), new ArrayParams()
                    .shape(shape)
                    .chunks(chunkSize)
                    .dataType(DataType.u2)
                    .fillValue(0)
                    .compressor(compressor)
            );

            createdArray.write(data, shape, offset);
        }
    }

    private static void testCompressorPerf() throws IOException, InvalidRangeException {
        setBloscCompressorThreads(8);

        ZarrArray array = ZarrArray.open(AIND_SAMPLE_DATASET.resolve(String.format("%d", 4)));

        int[] shape = array.getShape();
        int[] offset = {0, 0, 0, 0, 0};

        if (ZarrUtils.computeSize(shape) > Integer.MAX_VALUE) {
            shape[4] = (Integer.MAX_VALUE - 1) / shape[3] / shape[2];
        }

        Instant start = Instant.now();

        short[] data = (short[]) array.read(shape, offset);

        Duration duration = Duration.between(start, Instant.now());

        System.out.println(duration.toString());
    }

    private static void testChunkForLocation() throws IOException {
        Path path = AIND_SAMPLE_DATASET;

        OmeZarrGroup fileset = OmeZarrGroup.open(path);

        OmeZarrDataset dataset = fileset.getAttributes().getMultiscales()[0].getDatasets().get(6);

        OmeZarrReadChunk chunk = dataset.readChunkForLocation(new OmeZarrValue(0, 0, 274.8593, 57.87916, 236.3077));
    }
}
