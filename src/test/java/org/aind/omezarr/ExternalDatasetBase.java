package org.aind.omezarr;

import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ExternalDatasetBase {
    protected Path YX_SAMPLE_FILESET;

    protected Path TCZYX_SAMPLE_FILESET;

    private static final String YX_SAMPLE_NAME = "yx.ome.zarr";

    private static final  String TCZYX_SAMPLE_NAME = "tczyx.ome.zarr";

    @Before
    public void setUp(){
        YX_SAMPLE_FILESET = Paths.get(System.getProperty("TestSampleDir"), YX_SAMPLE_NAME );

        TCZYX_SAMPLE_FILESET = Paths.get(System.getProperty("TestSampleDir"), TCZYX_SAMPLE_NAME );
    }
}
