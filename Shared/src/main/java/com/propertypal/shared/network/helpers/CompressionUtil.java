package com.propertypal.shared.network.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class CompressionUtil
{
    public static byte[] ungzip(byte[] compressed) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
             GZIPInputStream gis = new GZIPInputStream(bais);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int read;
            while ((read = gis.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            return baos.toByteArray();
        }
    }
}
