package org.aind.omezarr;

public class OmeZarrAxis {
    private String name;
    private OmeZarrAxisType type;
    private OmeZarrAxisUnit unit = OmeZarrAxisUnit.NONE;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OmeZarrAxisType getType() {
        return type;
    }

    public void setType(OmeZarrAxisType type) {
        this.type = type;
    }

    public OmeZarrAxisUnit getUnit() {
        return unit;
    }

    public void setUnit(OmeZarrAxisUnit unit) {
        this.unit = unit;
    }
}
