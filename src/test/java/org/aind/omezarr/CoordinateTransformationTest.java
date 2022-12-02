package org.aind.omezarr;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

public class CoordinateTransformationTest {
    @Test
    public void TestDeserialization() throws IOException, URISyntaxException {
        URL myTestURL = ClassLoader.getSystemResource("coordinateTransformation.json");

        byte[] jsonData = Files.readAllBytes(Paths.get(myTestURL.toURI()));

        ObjectMapper objectMapper = new ObjectMapper();

        OmeZarrCoordinateTransformation ct = objectMapper.readValue(jsonData, OmeZarrCoordinateTransformation.class);

        Assert.assertEquals(OmeZarrCoordinateTransformationType.SCALE, ct.getType());

        Assert.assertArrayEquals(new double[] {1.0, 1.0, 1.8, 1.8, 2.0}, ct.getScale(), 0.001);
    }
}
