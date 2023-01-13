package org.aind.omezarr.image;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;

public class AutoContrast {
    public static void apply(DataBuffer dataBuffer) {
        AutoContrastParameters parameters = AutoContrastParameters.fromBuffer(dataBuffer);

        apply(dataBuffer, parameters);
    }

    public static void apply(DataBuffer dataBuffer, AutoContrastParameters parameters) {
        if (dataBuffer.getDataType() != DataBuffer.TYPE_USHORT) {
            throw new IllegalArgumentException("DataBuffer");
        }

        DataBufferUShort buffer = (DataBufferUShort) dataBuffer;

        for (int idx = 0; idx < buffer.getSize(); idx++) {
            int val = buffer.getElem(idx);

            val = (int) ((val - parameters.min) * parameters.slope);

            if (val < 0) {
                val = 0;
            } else if (val > 65535) {
                val = 65535;
            }

            buffer.setElem(idx, val);
        }
    }
}
