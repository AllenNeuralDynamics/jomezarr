package org.aind.omezarr;

import org.aind.omezarr.zarr.ExternalZarrStore;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OmeZarrGroup {
    private OmeZarrAttributes attributes;

    private final Path rootPath;

    private final ExternalZarrStore externalZarrStore;

    public static OmeZarrGroup open(String path) throws IOException {
        return open(Paths.get(path));
    }

    public static OmeZarrGroup open(URI uri) throws IOException {
        return open(Paths.get(uri.getPath()));
    }

    public static OmeZarrGroup open(Path path) throws IOException {
        return new OmeZarrGroup(path).readAttributes();
    }

    public static OmeZarrGroup open(ExternalZarrStore streamStoreProvider) throws IOException {
        return new OmeZarrGroup(streamStoreProvider).readAttributes();
    }

    public Path getRootPath() {
        return rootPath;
    }

    public ExternalZarrStore getStore() {
        return externalZarrStore;
    }

    public OmeZarrAttributes getAttributes() {
        return attributes;
    }

    protected OmeZarrGroup(Path rootPath) {
        this.rootPath = rootPath;

        this.externalZarrStore = null;
    }

    protected OmeZarrGroup(ExternalZarrStore externalZarrStore) {
        this.rootPath = null;

        this.externalZarrStore = externalZarrStore;
    }

    protected OmeZarrGroup readAttributes() throws IOException {
        if (rootPath != null) {
            attributes = OmeZarrAttributes.fromJson(rootPath.resolve(".zattrs"));
        } else if (externalZarrStore != null) {
            attributes = OmeZarrAttributes.fromInputStream(externalZarrStore.getInputStream(".zattrs"));
        }

        if (attributes != null) {
            attributes.setOmeZarrGroup(this);
        }

        return this;
    }
}
