package com.propertypal.network.responses;

import java.time.LocalDateTime;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.propertypal.network.GsonAdapters.LocalDateTimeAdapter;

public class BaseResponse
{
    public int STATUS;
    public LocalDateTime TIMESTAMP;

    public BaseResponse()
    {
        this.TIMESTAMP = LocalDateTime.now();
    }

    public String toJson()
    {
        //Setup Gson/Json parser
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe());
        Gson gson = gsonBuilder.create();

        try
        {
            return gson.toJson(this);
        }
        catch (Exception e)
        {
            System.out.println("ERROR: JSON failed construction: " + e.toString());
            return ("{'STATUS': " + Integer.toString(BaseResponseEnum.ERR_UNKNOWN) + ", 'TIMESTAMP': '" + LocalDateTime.now().toString() + "'}");
        }
    }
}