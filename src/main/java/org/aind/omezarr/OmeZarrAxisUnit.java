package org.aind.omezarr;

import com.fasterxml.jackson.annotation.JsonValue;

@SuppressWarnings("unused")
public enum OmeZarrAxisUnit {
    NONE("none"),
    ANGSTROM("angstrom"),
    ATTOMETER("attometer"),
    CENTIMETER("centimeter"),
    DECIMETER("decimeter"),
    EXAMETER("exameter"),
    FEMTOMETER("femtometer"),
    FOOT("foot"),
    GIGAMETER("gigameter"),
    HECTOMETER("hectometer"),
    INCH("inch"),
    KILOMETER("kilometer"),
    MEGAMETER("megameter"),
    METER("meter"),
    MICROMETER("micrometer"),
    MILE("mile"),
    MILLIMETER("millimeter"),
    NANOMETER("nanometer"),
    PARSEC("parsec"),
    PETAMETER("petameter"),
    PICOMETER("picometer"),
    TERAMETER("terameter"),
    YARD("yard"),
    YOCTOMETER("yoctometer"),
    YOTTAMETER("yottameter"),
    ZEPTOMETER("zeptometer"),
    ZETTAMETER("zettameter"),
    attosecond("attosecond"),
    centisecond("centisecond"),
    DAY("day"),
    DECISECOND("decisecond"),
    EXASECOND("exasecond"),
    FEMTOSECOND("femtosecond"),
    GIGASECOND("gigasecond"),
    HECTOSECOND("hectosecond"),
    HOUR("hour"),
    KILOSECOND("kilosecond"),
    MEGASECOND("megasecond"),
    MICROSECOND("microsecond"),
    MILLISECOND("millisecond"),
    MINUTE("minute"),
    NANOSECOND("nanosecond"),
    PETASECOND("petasecond"),
    PICOSECOND("picosecond"),
    SECOND("second"),
    TERASECOND("terasecond"),
    YOCTOSECOND("yoctosecond"),
    YOTTASECOND("yottasecond"),
    ZEPTOSECOND("zeptosecond"),
    ZETTASECOND("zettasecond");

    private final String name;

    OmeZarrAxisUnit(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
