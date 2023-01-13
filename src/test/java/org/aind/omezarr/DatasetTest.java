package org.aind.omezarr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import ucar.ma2.InvalidRangeException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DatasetTest extends ExternalDatasetBase {
    @Test
    public void TestDeserialization() throws IOException, URISyntaxException {
        URL myTestURL = ClassLoader.getSystemResource("dataset.json");

        byte[] jsonData = Files.readAllBytes(Paths.get(myTestURL.toURI()));

        ObjectMapper objectMapper = new ObjectMapper();

        OmeZarrDataset dataset = objectMapper.readValue(jsonData, OmeZarrDataset.class);

        Assert.assertEquals("0", dataset.getPath());

        Assert.assertNotNull(dataset.getCoordinateTransformations());

        Assert.assertEquals(2, dataset.getCoordinateTransformations().size());

        Assert.assertEquals(OmeZarrCoordinateTransformationType.TRANSLATION, dataset.getCoordinateTransformations().get(0).getType());

        Assert.assertEquals(OmeZarrCoordinateTransformationType.SCALE, dataset.getCoordinateTransformations().get(1).getType());
    }

    @Test
    public void TestReadShort2D() throws IOException, InvalidRangeException {
        Path path = YX_SAMPLE_FILESET;

        if (!Files.exists(path)) {
            return;
        }

        OmeZarrGroup fileset = OmeZarrGroup.open(path);

        short[] data = fileset.getAttributes().getMultiscales()[0].getDatasets().get(0).readShort();

        Assert.assertNotNull(data);

        Assert.assertEquals(952320, data.length);
    }

    @Test
    public void TestReadShort5D() throws IOException, InvalidRangeException {
        Path path = TCZYX_SAMPLE_FILESET;

        if (!Files.exists(path)) {
            return;
        }

        OmeZarrGroup fileset = OmeZarrGroup.open(path);

        short[] data = fileset.getAttributes().getMultiscales()[0].getDatasets().get(0).readShort(0, 0, 0);

        Assert.assertNotNull(data);

        Assert.assertEquals(134144, data.length);
    }
}
