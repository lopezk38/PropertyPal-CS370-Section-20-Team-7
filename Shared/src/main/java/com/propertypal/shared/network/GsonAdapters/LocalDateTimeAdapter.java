package com.propertypal.shared.network.GsonAdapters;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime>
{
    public LocalDateTime read(JsonReader reader) throws IOException
    {
        if (reader.peek() == JsonToken.NULL)
        {
            reader.nextNull();
            return null;
        }
        String timeStr = reader.nextString();

        LocalDateTime date = null;
        try
        {
            date = LocalDateTime.parse(timeStr);
        }
        catch (DateTimeParseException e)
        {
            date = LocalDateTime.MIN; //Default value is epoch
        }

        return date;
    }
    public void write(JsonWriter writer, LocalDateTime date) throws IOException
    {
        if (date == null)
        {
            writer.nullValue();
            return;
        }

        String timeStr = date.toString();
        writer.value(timeStr);
    }
}
