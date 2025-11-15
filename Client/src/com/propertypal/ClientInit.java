package com.propertypal;

import com.propertypal.APIHandler;
import com.propertypal.network.responses.*;
import com.propertypal.network.packets.*;

public class ClientInit
{
    public static void main(String[] args)
    {
        //TEMPORARY APIHANDLER TESTBENCH
        APIHandler netClient = APIHandler.getInstance();

        LoginPacket lPkt = new LoginPacket();
        lPkt.email = "1@2.com";
        lPkt.password = "pass"; //Real client would hash/salt the pass and store that in lPkt.password

        try
        {
            LoginResponse resp = netClient.sendRequest("/auth/login", lPkt, LoginResponse.class);
        }
        catch (Exception e)
        {
            System.out.println("Caught exception: " + e);
        }
        System.out.println("Login request completed");
        //END TESTBENCH
    }
}
