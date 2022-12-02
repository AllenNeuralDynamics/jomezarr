package org.aind.omezarr;

import org.aind.omezarr.image.OmeZarrImageStack;
import org.junit.Assert;
import org.junit.Test;
import ucar.ma2.InvalidRangeException;

import java.awt.image.Raster;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageStackTest extends ExternalDatasetBase {

    @Test
    public void TestLoadSlices()throws IOException, InvalidRangeException {
        Path path = TCZYX_SAMPLE_FILESET;

        if (!Files.exists(path)) {
            return;
        }

        OmeZarrGroup fileset = OmeZarrGroup.open(path);

        OmeZarrImageStack image = new OmeZarrImageStack(fileset.getAttributes().getMultiscales()[0].getDatasets()[0]);

        Raster[] slices = image.asSlices(0, 0, 2);

        Assert.assertNotNull(slices);

        Assert.assertEquals(2, slices.length);
    }
}
