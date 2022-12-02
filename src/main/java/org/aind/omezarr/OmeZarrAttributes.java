package org.aind.omezarr;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
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

    public static OmeZarrAttributes fromJson(String path) throws IOException {
        return fromJson(Paths.get(path));
    }

    public static OmeZarrAttributes fromJson(Path path) throws IOException {
        byte[] jsonData = Files.readAllBytes(path);

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(jsonData, OmeZarrAttributes.class);
    }
}
