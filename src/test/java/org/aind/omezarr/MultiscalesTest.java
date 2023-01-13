package org.aind.omezarr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MultiscalesTest {
    @Test
    public void TestDeserialization() throws IOException, URISyntaxException {
        URL myTestURL = ClassLoader.getSystemResource("multiscales.json");

        byte[] jsonData = Files.readAllBytes(Paths.get(myTestURL.toURI()));

        ObjectMapper objectMapper = new ObjectMapper();

        OmeZarrMultiscale[] multiscales = (OmeZarrMultiscale[]) objectMapper.readValue(jsonData, Array.newInstance(OmeZarrMultiscale.class, 0).getClass());

        Assert.assertNotNull(multiscales);

        Assert.assertEquals(1, multiscales.length);

        Assert.assertEquals(5, multiscales[0].getAxes().size());
        Assert.assertEquals(4, multiscales[0].getDatasets().size());

        Assert.assertEquals("/sample.zarr", multiscales[0].getName());
        Assert.assertEquals("0.4", multiscales[0].getVersion());

        List<Integer> spatialIndices = multiscales[0].getSpatialIndices();

        Assert.assertEquals(2, (int)spatialIndices.get(0));
        Assert.assertEquals(3, (int)spatialIndices.get(1));
        Assert.assertEquals(4, (int)spatialIndices.get(2));

        Assert.assertEquals(1.8, multiscales[0].getDatasets().get(0).getMinSpatialResolution(), 0.001);
    }

    private void VerifyAxis(OmeZarrAxis axis, String name, OmeZarrAxisType type, OmeZarrAxisUnit unit) {
        Assert.assertEquals(name, axis.getName());
        Assert.assertEquals(type, axis.getType());
        Assert.assertEquals(unit, axis.getUnit());
    }
}
