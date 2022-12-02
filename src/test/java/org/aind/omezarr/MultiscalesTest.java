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

public class MultiscalesTest {
    @Test
    public void TestDeserialization() throws IOException, URISyntaxException {
        URL myTestURL = ClassLoader.getSystemResource("multiscales.json");

        byte[] jsonData = Files.readAllBytes(Paths.get(myTestURL.toURI()));

        ObjectMapper objectMapper = new ObjectMapper();

        OmeZarrMultiscale[] multiscales = (OmeZarrMultiscale[]) objectMapper.readValue(jsonData, Array.newInstance(OmeZarrMultiscale.class, 0).getClass());

        Assert.assertNotNull(multiscales);

        Assert.assertEquals(1, multiscales.length);

        Assert.assertEquals(5, multiscales[0].getAxes().length);
        Assert.assertEquals(4, multiscales[0].getDatasets().length);

        Assert.assertEquals("/sample.zarr", multiscales[0].getName());
        Assert.assertEquals("0.4", multiscales[0].getVersion());
    }

    private void VerifyAxis(OmeZarrAxis axis, String name, OmeZarrAxisType type, OmeZarrAxisUnit unit) {
        Assert.assertEquals(name, axis.getName());
        Assert.assertEquals(type, axis.getType());
        Assert.assertEquals(unit, axis.getUnit());
    }
}
