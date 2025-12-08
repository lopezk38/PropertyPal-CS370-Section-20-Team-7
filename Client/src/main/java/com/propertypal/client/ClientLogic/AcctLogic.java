package com.propertypal.client.ClientLogic;

import com.propertypal.client.APIHandler;
import com.propertypal.client.SessionManager;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.responses.*;

import java.io.IOException;

public class AcctLogic
{
    private static AcctLogic instance = null;
    private APIHandler handler = null;

    private AcctLogic()
    {
        instance = this;
        handler = APIHandler.getInstance();
    }

    public static AcctLogic getInstance()
    {
        if (instance == null)
        {
            instance = new AcctLogic();
        }

        return instance;
    }

    //this function tries to log in the in user
    //throws exception if fails, otherwise returns the users role
    public SessionManager.Role loginAndGetRole(String email, String pass) throws IOException
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

        return loginResp.IS_LANDLORD ? SessionManager.Role.LANDLORD : SessionManager.Role.TENANT;
    }

    public boolean isLeaseReady()
    {
        //Attempt to get leaseID
        Long result = getLeaseID();

        return result != null;
    }

    public Long getLeaseID()
    {
        GetAcctLeasePacket packet = new GetAcctLeasePacket();

        try
        {
            GetAcctLeaseResponse resp = handler.sendRequest("/account/getLease", packet, GetAcctLeaseResponse.class);
            if (resp.STATUS == 0)
            {
                return resp.LEASE;
            }
        } catch (IOException e)
        {
            return null;
        }

        return null;
    }

    public GetLeaseContactsResponse getContacts(Long leaseID) throws IOException
    {
        GetLeaseContactsPacket packet = new GetLeaseContactsPacket();
        packet.lease_id = leaseID;

        GetLeaseContactsResponse resp = handler.sendRequest("/lease/getContacts", packet, GetLeaseContactsResponse.class);
        if (resp.STATUS != 0)
        {
            throw new IOException("Got bad response from server for contact info");
        }

        if (resp.LL_FNAME == null)
        {
            resp.LL_FNAME = "ERROR";
        }
        if (resp.LL_LNAME == null)
        {
            resp.LL_LNAME = "ERROR";
        }
        if (resp.LL_EMAIL == null)
        {
            resp.LL_EMAIL = "ERROR";
        }
        if (resp.LL_PHONE == null)
        {
            resp.LL_PHONE = "ERROR";
        }
        if (resp.TT_FNAME == null)
        {
            resp.TT_FNAME = "ERROR";
        }
        if (resp.TT_LNAME == null)
        {
            resp.TT_LNAME = "ERROR";
        }
        if (resp.TT_EMAIL == null)
        {
            resp.TT_EMAIL = "ERROR";
        }
        if (resp.TT_PHONE == null)
        {
            resp.TT_PHONE = "ERROR";
        }

        return resp;
    }

    public CreateAcctResponse createAccount(
            String email,
            String password,
            String firstName,
            String lastName,
            String phone,
            boolean isLandlord,
            String street,
            String city,
            String state,
            String zip
    ) throws IOException
    {
        CreateAcctPacket pkt = new CreateAcctPacket();

        // Required fields
        pkt.email = email;
        pkt.password = password;
        pkt.firstName = firstName;
        pkt.lastName = lastName;
        pkt.phone = phone;

        // Landlord fields
        if (isLandlord)
        {
            pkt.propAddr1 = street;
            pkt.propCity = city;
            pkt.propState = state;
            pkt.propZip = zip;
            pkt.propCountry = "US"; // Hardcoded
        }

        // Decide endpoint based on account type
        String endpoint = isLandlord ? "/auth/newAcct/landlord" : "/auth/newAcct/tenant";

        CreateAcctResponse resp = handler.sendRequest(endpoint, pkt, CreateAcctResponse.class);

        if (resp.STATUS != 0)
        {
            throw new IOException("CreateAcct failed. STATUS = " + resp.STATUS);
        }

        return resp;
    }
}