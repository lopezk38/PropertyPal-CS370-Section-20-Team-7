package com.propertypal;

import java.time.LocalDateTime;
import java.io.IOException;

import com.propertypal.network.responses.*;
import com.propertypal.network.packets.*;

public class ClientRequest
{
    public BasePacket packet;
    private BaseResponse response = null;
    private Endpoint source = null;


    public ClientRequest(Endpoint source, BasePacket packet)
    {
        this.source = source;
        this.packet = packet;
    }

    public void setResponse(BaseResponse response)
    {
        this.response = response;
    }

    public void sendResponse() throws IllegalStateException
    {
        if (source == null)
        {
            throw new IllegalStateException("Cannot send a response multiple times");
        }

        if (response == null)
        {
            setUnknownErrResponse();
        }

        String respStr = null;
        try
        {
            respStr = response.toJson();
        }
        catch (Exception e)
        {
            System.out.println("ERROR: Failed to serialize response");
            respStr = ("{'STATUS': " + Integer.toString(BaseResponseEnum.ERR_UNKNOWN) + ", 'TIMESTAMP': '" + LocalDateTime.now().toString() + "'}");
        }

        source.sendResp(respStr);

        //Decommission this instance
        source = null;
    }

    public String getRemoteIP()
    {
        if (source == null) return null;

        return source.getRemoteIP().toString();
    }

    public void setUnknownErrResponse()
    {
        response = new BaseResponse();
        response.STATUS = BaseResponseEnum.ERR_UNKNOWN;
    }

    public void setBaseErrResponse(int status)
    {
        this.response = new BaseResponse();
        response.STATUS = status;
    }
}
