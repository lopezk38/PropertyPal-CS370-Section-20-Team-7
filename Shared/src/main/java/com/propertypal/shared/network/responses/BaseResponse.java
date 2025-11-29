package com.propertypal.shared.network.responses;

import java.time.LocalDateTime;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import com.propertypal.shared.network.GsonWrapper;
import com.propertypal.shared.network.enums.BaseResponseEnum;

public class BaseResponse
{
    public Integer STATUS;
    public LocalDateTime TIMESTAMP;

    public BaseResponse()
    {
        this.TIMESTAMP = LocalDateTime.now();
    }

    public String toJson()
    {
        Gson gson = GsonWrapper.getInstance();

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

    public static <T extends BaseResponse> T fromJson(String jsonStr, Class<T> type) throws IllegalArgumentException, JsonSyntaxException
    {
        Gson gson = GsonWrapper.getInstance();

        return gson.fromJson(jsonStr, type);
    }
}