package org.aind.omezarr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OmeZarrMultiscale {
    private OmeZarrAttributes attributes;

    private String name;

    private String version;

    private OmeZarrAxis[] axes;

    private OmeZarrDataset[] datasets;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public OmeZarrAxis[] getAxes() {
        return axes;
    }

    public void setAxes(OmeZarrAxis[] axes) {
        this.axes = axes;
    }

    public OmeZarrDataset[] getDatasets() {
        return datasets;
    }

    public void setDatasets(OmeZarrDataset[] datasets) {
        this.datasets = datasets;

        for (OmeZarrDataset dataset : datasets) {
            dataset.setMultiscale(this);
        }
    }

    public OmeZarrAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(OmeZarrAttributes attributes) {
        this.attributes = attributes;
    }
}
