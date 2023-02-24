package org.aind.omezarr;

import org.aind.omezarr.zarr.ExternalZarrStore;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.TreeSet;
import java.util.stream.Stream;

public class ExternalZarrStoreTest {
    @Test
    public void TestStreamDeserialization() throws IOException, URISyntaxException {
        URL resourceUrl = ClassLoader.getSystemResource("omezarrzattr.json");

        FileWrapperStore store = new FileWrapperStore(resourceUrl);

        OmeZarrGroup group = OmeZarrGroup.open(store);

        Assert.assertNotNull(group);

        Assert.assertNull(group.getRootPath());

        Assert.assertNull(group.getAttributes().getMultiscales()[0].getDatasets().get(0).getParentPath());
    }

    private class FileWrapperStore extends ExternalZarrStore {
        private URL root;

        public FileWrapperStore(URL root) {
            super("");

            this.root = root;
        }

        @Override
        public InputStream getInputStream(String s) throws IOException {
            return root.openStream();
        }

        @Override
        public OutputStream getOutputStream(String s) throws IOException {
            return null;
        }

        @Override
        public void delete(String s) throws IOException {

        }

        @Override
        public TreeSet<String> getArrayKeys() throws IOException {
            return null;
        }

        @Override
        public TreeSet<String> getGroupKeys() throws IOException {
            return null;
        }

        @Override
        public TreeSet<String> getKeysEndingWith(String s) throws IOException {
            return null;
        }

        @Override
        public Stream<String> getRelativeLeafKeys(String s) throws IOException {
            return null;
        }
    }
}
