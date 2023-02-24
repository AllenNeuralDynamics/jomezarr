package org.aind.omezarr.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamUtil {
    public static byte[] readAllBytes(InputStream stream) throws IOException {
        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                final int bufferLength = 4096;

                byte[] buffer = new byte[bufferLength];

                int numBytesRead;

                while ((numBytesRead = stream.read(buffer, 0, bufferLength)) != -1)
                    outputStream.write(buffer, 0, numBytesRead);

                return outputStream.toByteArray();
            }
        } finally {
            stream.close();
        }
    }
}
