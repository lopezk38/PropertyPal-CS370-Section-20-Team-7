package com.propertypal.client.ClientLogic;

import com.propertypal.client.APIHandler;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.responses.*;

import java.io.IOException;

public class PaymentLogic
{
    private static PaymentLogic instance = null;
    private APIHandler handler = null;

    private PaymentLogic()
    {
        handler = APIHandler.getInstance(); //get shared APIHandler instance for sending network requests
    }

    public static PaymentLogic getInstance()
    {
        if (instance == null)
        {
            instance = new PaymentLogic();
        }

        return instance;
    }

    public String getPayPalLink(Long leaseID) throws IOException, IllegalArgumentException
    {
        if (leaseID == null || leaseID < 1)
        {
            throw new IllegalArgumentException("Invalid lease ID");
        }

        GetPayLinkPacket packet = new GetPayLinkPacket();
        packet.lease_id = leaseID;

        GetPayLinkResponse resp = handler.sendRequest("/lease/getPayLink", packet, GetPayLinkResponse.class);
        if (resp.STATUS != 0)
        {
            throw new IOException("Server did not return a paylink");
        }

        return resp.PAYLINK;
    }
}
