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

    //Lease contact info
    private String llFname;
    private String llLname;
    private String llEmail;
    private String llPhone;
    private String ttFname;
    private String ttLname;
    private String ttEmail;
    private String ttPhone;

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

    //Contact info getters
    public String getLLFname() { return llFname; }
    public String getLLLname() { return llLname; }
    public String getLLEmail() { return llEmail; }
    public String getLLPhone() { return llPhone; }
    public String getTTFname() { return ttFname; }
    public String getTTLname() { return ttLname; }
    public String getTTEmail() { return ttEmail; }
    public String getTTPhone() { return ttPhone; }

    //Packet generators and handlers
    //Tickets
    public List<Long> getTicketIDList(long leaseID) throws IOException
    {
        List<Long> list = ticketLogic.getTicketIDList(leaseID);
        return list;
    }

    public CreateTicketResponse createTicket(long leaseID, String title, String description) throws IOException
    {
        CreateTicketResponse resp = ticketLogic.createTicket(leaseID, title, description);
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
        getContacts();
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

    public void getContacts()
    {
        try
        {
            GetLeaseContactsResponse resp = acctLogic.getContacts(leaseID);
            llFname = resp.LL_FNAME;
            llLname = resp.LL_LNAME;
            llEmail = resp.LL_EMAIL;
            llPhone = resp.LL_PHONE;
            ttFname = resp.TT_FNAME;
            ttLname = resp.TT_LNAME;
            ttEmail = resp.TT_EMAIL;
            ttPhone = resp.TT_PHONE;
        }
        catch (IOException e)
        {
            System.out.println("WARNING: Failed to retrieve contact info from server");
        }
        return;
    }

    //Documents
    //TODO

    //Payments
    //TODO
}