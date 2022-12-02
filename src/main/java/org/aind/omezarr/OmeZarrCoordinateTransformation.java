package org.aind.omezarr;

import java.util.Arrays;

public class OmeZarrCoordinateTransformation {
    private double[] scale;

    private double[] translation;

    private OmeZarrCoordinateTransformationType type;

    public OmeZarrCoordinateTransformationType getType() {
        return type;
    }

    @SuppressWarnings("unused")
    public void setType(OmeZarrCoordinateTransformationType type) {
        this.type = type;
    }

    @SuppressWarnings("unused")
    public double[] getTranslation() {
        return translation;
    }

    @SuppressWarnings("unused")
    public void setTranslation(double[] translation) {
        this.translation = translation;
    }

    public double[] getScale() {
        return scale;
    }

    @SuppressWarnings("unused")
    public void setScale(double[] scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        if (type == OmeZarrCoordinateTransformationType.IDENTITY) {
            return type.getName();
        }
        else {
            double[] array = type == OmeZarrCoordinateTransformationType.SCALE ? scale : translation;

            return type.getName()
                    + ": ["
                    + String.join(", ", Arrays.stream(array).mapToObj(String::valueOf).toArray(String[]::new))
                    + "]";
        }
    }
}
