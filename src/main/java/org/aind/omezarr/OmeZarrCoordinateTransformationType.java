package org.aind.omezarr;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OmeZarrCoordinateTransformationType {
    IDENTITY("identity"),
    TRANSLATION("translation"),
    SCALE("scale");

    private final String name;

    OmeZarrCoordinateTransformationType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName(){
        return name;
    }
}
