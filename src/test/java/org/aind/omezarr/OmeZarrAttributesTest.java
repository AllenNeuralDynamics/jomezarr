package org.aind.omezarr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OmeZarrAttributesTest {
    @Test
    public void TestDeserialization() throws IOException, URISyntaxException {
        URL resourceUrl = ClassLoader.getSystemResource("omezarrzattr.json");

        OmeZarrAttributes attr = OmeZarrAttributes.fromJson(Paths.get(resourceUrl.toURI()));

        Assert.assertNotNull(attr);

        Assert.assertEquals(1, attr.getMultiscales().length);
    }

    @Test
    public void TestStreamDeserialization() throws IOException, URISyntaxException {
        URL resourceUrl = ClassLoader.getSystemResource("omezarrzattr.json");

        InputStream input = resourceUrl.openStream();

        OmeZarrAttributes attr = OmeZarrAttributes.fromInputStream(input);

        Assert.assertNotNull(attr);

        Assert.assertEquals(1, attr.getMultiscales().length);

        input.close();
    }
}
