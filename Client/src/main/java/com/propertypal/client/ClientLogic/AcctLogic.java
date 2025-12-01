package com.propertypal.client.ClientLogic;

import com.propertypal.shared.network.packets.*;
import com.propertypal.client.APIHandler;
import com.propertypal.shared.network.responses.*;

import java.io.IOException;

public class AcctLogic
{
    APIHandler handler = null;

    public AcctLogic() //TODO Change back to private after facade is made
    {
        handler = APIHandler.getInstance();

    }

    //this function tries to login the in user
    //throws exception if fails, otherwise returns the users role
    public Boolean loginAndGetRole(String email, String pass) throws IOException
    {

        //get login token and success status
        LoginPacket loginPkt = new LoginPacket();
        loginPkt.email = email;
        loginPkt.password = pass;
        LoginResponse loginResp = handler.sendRequest("/auth/login", loginPkt, LoginResponse.class);

        if (loginResp.STATUS != 0)
        {
            throw new IOException("Login failed with error: " + Integer.toString(loginResp.STATUS));
        }

        return loginResp.IS_LANDLORD;

    }
}
