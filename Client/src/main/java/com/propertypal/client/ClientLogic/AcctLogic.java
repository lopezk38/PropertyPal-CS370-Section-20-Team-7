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

    public boolean acctLogin(String email, String pass) throws IOException
    {
        LoginPacket loginPkt = new LoginPacket();
        boolean loginSuccess = false;
        loginPkt.email = email;
        loginPkt.password = pass;

        BaseResponse resp;
        resp = handler.sendRequest("/auth/login", loginPkt, LoginResponse.class);

        if(resp.STATUS != 0)
        {
            //TODO error
            throw new IOException("Login failed with error: " + Integer.toString(resp.STATUS));
        }
        else
        {
            System.out.println("Token successfully received");
            loginSuccess = true;
            return loginSuccess;

        }

        //example for future client logics like GetTicketList
        //LoginResponse loginResp = (LoginResponse) resp;
    }
}
