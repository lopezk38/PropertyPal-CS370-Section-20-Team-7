package com.propertypal.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;

import com.propertypal.network.GsonAdapters.LocalDateTimeAdapter;

public class GsonWrapper
{
    private static Gson instance = null;

    private static void init()
    {
        if (instance != null) return;

        //Setup Gson/Json parser
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe());
        instance = gsonBuilder.create();
    }

    public static Gson getInstance()
    {
        if (instance == null) { init(); }

        return instance;
    }
}
