package com.propertypal.shared.network.packets;

import com.propertypal.shared.network.GsonAdapters.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.propertypal.shared.network.GsonWrapper;

public class BasePacket
{
    public String token = null;
    public LocalDateTime timestamp = null;

    public BasePacket()
    {
        timestamp = LocalDateTime.now();
    }

    public String toJson() throws IllegalArgumentException
    {
        Gson gson = GsonWrapper.getInstance();

        return gson.toJson(this);
    }

    public static <T extends BasePacket> T fromJson(String jsonStr, Class<T> type) throws IllegalArgumentException, JsonSyntaxException
    {
        Gson gson = GsonWrapper.getInstance();

        return gson.fromJson(jsonStr, type);
    }
}