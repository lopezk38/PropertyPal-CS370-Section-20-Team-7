package com.propertypal.network.responses;

import java.time.LocalDateTime;
import com.google.gson.Gson;

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
            return ("{'STATUS': " + BaseResponseEnum.ERR_UNKNOWN.toString() + ", 'TIMESTAMP': '" + LocalDateTime.now().toString() + "'}");
        }
    }
}