package org.aind.omezarr;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OmeZarrAxisType {
    TIME("time"),
    SPACE("space"),
    CHANNEL("channel");

    private final String name;

    OmeZarrAxisType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName(){
        return name;
    }
}
