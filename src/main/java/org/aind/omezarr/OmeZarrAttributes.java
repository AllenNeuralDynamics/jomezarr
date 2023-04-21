package org.aind.omezarr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aind.omezarr.util.InputStreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OmeZarrAttributes {

    private OmeZarrGroup fileset;

    private OmeZarrMultiscale[] multiscales;

    private Object omero;

    public OmeZarrMultiscale[] getMultiscales() {
        return multiscales;
    }

    public void setMultiscales(OmeZarrMultiscale[] multiscales) {
        this.multiscales = multiscales;

        for (OmeZarrMultiscale multiscale: multiscales) {
            multiscale.setAttributes(this);
        }
    }

    public Object getOmero() {
        return omero;
    }

    public void setOmero(Object omero) {
        this.omero = omero;
    }

    public OmeZarrGroup getFileset() {
        return fileset;
    }

    public void setFileset(OmeZarrGroup fileset) {
        this.fileset = fileset;
    }

    public static OmeZarrAttributes fromInputStream(InputStream stream) throws IOException {
        return mapObjectFromBytes(InputStreamUtil.readAllBytes(stream));
    }

    public static OmeZarrAttributes fromJson(String path) throws IOException {
        return fromJson(Paths.get(path));
    }

    public static OmeZarrAttributes fromJson(Path path) throws IOException {
        return mapObjectFromBytes(Files.readAllBytes(path));
    }

    private static OmeZarrAttributes mapObjectFromBytes(byte[] bytes) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(bytes, OmeZarrAttributes.class);
    }
}
