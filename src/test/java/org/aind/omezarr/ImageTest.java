package org.aind.omezarr;

import org.aind.omezarr.image.OmeZarrImage;
import org.junit.Assert;
import org.junit.Test;
import ucar.ma2.InvalidRangeException;

import java.awt.image.Raster;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageTest extends ExternalDatasetBase {

    @Test
    public void TestCreateRaster() throws IOException, InvalidRangeException {
        Path path = TCZYX_SAMPLE_FILESET;

        if (!Files.exists(path)) {
            return;
        }

        OmeZarrGroup fileset = OmeZarrGroup.open(path);

        OmeZarrImage image = new OmeZarrImage(fileset.getAttributes().getMultiscales()[0].getDatasets().get(0), 0, 0, 0);

        Raster raster = image.asRaster();

        Assert.assertNotNull(raster);
    }
}
