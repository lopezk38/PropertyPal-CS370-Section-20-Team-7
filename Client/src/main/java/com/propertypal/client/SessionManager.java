package com.propertypal.client;

import com.propertypal.client.ClientLogic.AcctLogic;
import com.propertypal.client.ClientLogic.TicketLogic;
import com.propertypal.shared.network.responses.*;

import java.io.IOException;
import java.util.List;

public class SessionManager
{
    // Singleton instance
    private static SessionManager instance;

    //Subcontrollers
    private TicketLogic ticketLogic = null;
    private AcctLogic acctLogic = null;

    // User info
    private String username;
    private Role role;

    //Session info
    Long leaseID;

    public enum Role
    {
        TENANT,
        LANDLORD
    }

    // Private constructor
    private SessionManager()
    {
        ticketLogic = TicketLogic.getInstance();
        acctLogic = AcctLogic.getInstance();
    }

    // Get singleton instance
    public static SessionManager getInstance()
    {
        if (instance == null)
        {
            instance = new SessionManager();
        }
        return instance;
    }

    // Set user info after login
    public void login(String username, Role role)
    {
        this.username = username;
        this.role = role;
    }

    // Clear session
    public void logout()
    {
        this.username = null;
        this.role = null;
    }

    // Getters
    public String getUsername()
    {
        return username;
    }

    public Role getRole()
    {
        return role;
    }

    public Long getLeaseID() { return leaseID; }

    public boolean isTenant()
    {
        return role == Role.TENANT;
    }

    public boolean isLandlord()
    {
        return role == Role.LANDLORD;
    }

    //Packet generators and handlers
    //Tickets
    public List<Long> getTicketIDList(long leaseID) throws IOException
    {
        List<Long> list = ticketLogic.getTicketIDList(leaseID);
        return list;
    }

    public CreateTicketResponse createTicket(long leaseID, String description) throws IOException
    {
        CreateTicketResponse resp = ticketLogic.createTicket(leaseID, description);
        return resp;
    }

    public GetTicketInfoResponse getTicketInfo(long ticketID) throws IOException
    {
        GetTicketInfoResponse resp = ticketLogic.getTicketInfo(ticketID);
        return resp;
    }

    public void closeTicket(long ticketID) throws IOException
    {
        ticketLogic.closeTicket(ticketID);
    }

    //Accounts
    public Role loginAndGetRole(String email, String pass) throws IOException
    {
        Role role = acctLogic.loginAndGetRole(email, pass);
        this.username = email;
        this.role = role;
        this.leaseID = acctLogic.getLeaseID();
        return role;
    }

    public CreateAcctResponse createTenantAccount() throws IOException //TODO add params
    {
        //TODO do request

        this.leaseID = acctLogic.getLeaseID();

        return null; //TODO replace this with real logic
    }

    public CreateAcctResponse CreateLandlordAccount() throws IOException //TODO add params
    {
        //TODO do request

        this.leaseID = acctLogic.getLeaseID();

        return null; //TODO replace this with real logic
    }

    //Documents
    //TODO

    //Payments
    //TODO
}