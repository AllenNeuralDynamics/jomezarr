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

public class AxesTest {
    @Test
    public void TestDeserialization() throws IOException, URISyntaxException {
        URL myTestURL = ClassLoader.getSystemResource("axes.json");

        byte[] jsonData = Files.readAllBytes(Paths.get(myTestURL.toURI()));

        ObjectMapper objectMapper = new ObjectMapper();

        OmeZarrAxis[] axes = (OmeZarrAxis[]) objectMapper.readValue(jsonData, Array.newInstance(OmeZarrAxis.class, 0).getClass());

        Assert.assertNotNull(axes);

        Assert.assertEquals(5, axes.length);

        VerifyAxis(axes[0], "t", OmeZarrAxisType.TIME, OmeZarrAxisUnit.MILLISECOND);
        VerifyAxis(axes[1], "c", OmeZarrAxisType.CHANNEL, OmeZarrAxisUnit.NONE);
        VerifyAxis(axes[2], "z", OmeZarrAxisType.SPACE, OmeZarrAxisUnit.MICROMETER);
        VerifyAxis(axes[3], "y", OmeZarrAxisType.SPACE, OmeZarrAxisUnit.MICROMETER);
        VerifyAxis(axes[4], "x", OmeZarrAxisType.SPACE, OmeZarrAxisUnit.MICROMETER);
    }

    private void VerifyAxis(OmeZarrAxis axis, String name, OmeZarrAxisType type, OmeZarrAxisUnit unit) {
        Assert.assertEquals(name, axis.getName());
        Assert.assertEquals(type, axis.getType());
        Assert.assertEquals(unit, axis.getUnit());
    }
}
