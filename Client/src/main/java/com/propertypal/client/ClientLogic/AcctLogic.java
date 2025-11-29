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

    public Integer acctLogin(String email, String pass) throws IOException
    {
        boolean loginSuccess = false;

        //get login token and success status
        LoginPacket loginPkt = new LoginPacket();
        loginPkt.email = email;
        loginPkt.password = pass;
        LoginResponse loginResp = handler.sendRequest("/auth/login", loginPkt, LoginResponse.class);
        String tkn = loginResp.TOKEN;

        if(loginResp.STATUS != 0) {throw new IOException("Login failed with error: " + Integer.toString(loginResp.STATUS));}

        //get acct lease to get role
        GetAcctLeasePacket leasePkt = new GetAcctLeasePacket();
        leasePkt.token = tkn;
        GetAcctLeaseResponse leaseResp = handler.sendRequest("/account/getLease", leasePkt, GetAcctLeaseResponse.class);
        Long acctLease = leaseResp.LEASE;

        if(leaseResp.STATUS != 0) {throw new IOException("Getting lease failed with error: " + Integer.toString(leaseResp.STATUS));}

        //get role
        GetRolePacket rolePkt = new GetRolePacket();
        rolePkt.token = tkn;
        rolePkt.lease_id = acctLease;
        GetRoleResponse roleResp = handler.sendRequest("/lease/getRole", rolePkt, GetRoleResponse.class);

        if(roleResp.STATUS != 0) {throw new IOException("Getting role failed with error: " + Integer.toString(roleResp.STATUS));}

        //LL = 0, TT = 1
        Integer theRoleOfCurrentLogin = roleResp.ROLE;

        if(theRoleOfCurrentLogin != null)
        {
            return theRoleOfCurrentLogin;
        }
        else
        {
            throw new IOException("Error: Failed to navigate to correct page because " +
                    "the value of account's role is: " + null);
        }


        //example for future client logics like GetTicketList
        //LoginResponse loginResp = (LoginResponse) resp;
    }


}
