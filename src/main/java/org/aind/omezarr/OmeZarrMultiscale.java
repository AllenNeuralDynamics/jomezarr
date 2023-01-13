package org.aind.omezarr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Not yet supported - multiscale-level coordinateTransform, metadata, and type properties.

@JsonIgnoreProperties(ignoreUnknown = true)
public class OmeZarrMultiscale {
    private OmeZarrAttributes attributes;

    private String name;

    private String version;

    private ArrayList<OmeZarrAxis> axes;

    private ArrayList<OmeZarrDataset> datasets;

    private List<Integer> spatialIndices;

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

    public ArrayList<OmeZarrAxis> getAxes() {
        return axes;
    }

    public void setAxes(ArrayList<OmeZarrAxis> axes) {
        this.axes = axes;

        spatialIndices = IntStream.range(0, axes.size())
                .filter(i -> axes.get(i).getType() == OmeZarrAxisType.SPACE)
                .mapToObj(i -> i)
                .collect(Collectors.toList());
    }

    public ArrayList<OmeZarrDataset> getDatasets() {
        return datasets;
    }

    public void setDatasets(ArrayList<OmeZarrDataset> datasets) {
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

    public List<Integer> getSpatialIndices() {
        return spatialIndices;
    }
}
