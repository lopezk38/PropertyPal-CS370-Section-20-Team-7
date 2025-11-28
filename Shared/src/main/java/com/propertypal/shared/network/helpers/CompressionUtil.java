package com.propertypal.shared.network.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtil
{
    public static byte[] ungzip(byte[] compressed) throws IOException
    {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
             GZIPInputStream gis = new GZIPInputStream(bais);
             ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {

            byte[] buffer = new byte[4096];
            int read;
            while ((read = gis.read(buffer)) != -1)
            {
                baos.write(buffer, 0, read);
            }
            return baos.toByteArray();
        }
    }

    public static byte[] gzip(byte[] data) throws IOException
    {
        if (data == null || data.length == 0)
        {
            return data;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (GZIPOutputStream gos = new GZIPOutputStream(baos))
        {
            gos.write(data);
            // gos.close() is called automatically by try-with-resources,
            // which also finishes the gzip stream.
        }

        return baos.toByteArray();
    }
}
