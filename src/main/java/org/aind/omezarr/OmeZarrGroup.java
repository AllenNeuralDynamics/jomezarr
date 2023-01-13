package org.aind.omezarr;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OmeZarrGroup {
    private OmeZarrAttributes attributes;

    private Path path;

    public static OmeZarrGroup open(String path) throws IOException {
        return open(Paths.get(path));
    }

    public static OmeZarrGroup open(URI uri) throws IOException {
        return open(Paths.get(uri.getPath()));
    }

    public static OmeZarrGroup open(Path path) throws IOException {
        return new OmeZarrGroup(path, OmeZarrAttributes.fromJson(path.resolve(".zattrs")));
    }

    public Path getPath() {
        return path;
    }

    public OmeZarrAttributes getAttributes() {
        return attributes;
    }

    protected OmeZarrGroup(Path path, OmeZarrAttributes attributes) {
        this.path = path;

        this.attributes = attributes;

        this.attributes.setFileset(this);
    }
}
