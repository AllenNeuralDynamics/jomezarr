package org.aind.omezarr.zarr;

import com.bc.zarr.storage.Store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TreeSet;
import java.util.stream.Stream;

public class ExternalZarrStore implements Store {

    protected final String prefix;

    public ExternalZarrStore(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public InputStream getInputStream(String s) throws IOException {
        return null;
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
