package com.propertypal;

import com.propertypal.LogicBlocks.*;

import javax.print.Doc;

public class CoreLogic
{
    private static CoreLogic instance = null;

    private AuthLogic authLogic = null;
    private AccountLogic acctLogic = null;
    private DocLogic docLogic = null;
    private TicketLogic ticketLogic = null;

    private CoreLogic()
    {
        if (instance != null) return;

        instance = this;

        authLogic = new AuthLogic();
        acctLogic = new AccountLogic();
        docLogic = new DocLogic();
        ticketLogic = new TicketLogic();
    }

    public static CoreLogic getInstance()
    {
        if (instance == null)
        {
            instance = new CoreLogic();
        }

        return instance;
    }

    //AuthLogic
    public void handleLogin(ClientRequest req) { authLogic.handleLogin(req); }
    public void handleLogout(ClientRequest req) { ; } //TODO: Create and connect to submodule

    //AccountLogic
    public void handleCreateTenantAcct(ClientRequest req) { acctLogic.handleCreateTenantAccount(req); }
    public void handleCreateLandlordAcct(ClientRequest req) { acctLogic.handleCreateLandlordAccount(req); }
    public void handleCreateInvite(ClientRequest req) { acctLogic.handleCreateInvite(req); }
    public void handleAcceptInvite(ClientRequest req) { acctLogic.handleAcceptInvite(req); }
    public void handleGetInviteList(ClientRequest req) { acctLogic.handleGetInviteList(req); }
    public void handleUploadDoc(ClientRequest req) { docLogic.handleUploadDoc(req); }

    //DocLogic
    public void handleViewDoc(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleEditDoc(ClientRequest req) { ; } //TODO: Create and connect to submodule

    //TicketLogic
    public void handleCreateTicket(ClientRequest req) { ticketLogic.handleCreateTicketPacket(req); } //TODO: Create and connect to submodule
    public void handleEditTicket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleViewTicket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleGetTicketList(ClientRequest req) { ticketLogic.handleGetTicketList(req); } //TODO

    //PaymentLogic
    public void handleRequestRent(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleUpdAmountDue(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handlePayRent(ClientRequest req) { ; } //TODO: Create and connect to submodule

    //NavigationLogic
    public void handleTenantLandingPgInfo(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleLandlordLandingPgInfo(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleNotifications(ClientRequest req) { ; } //TODO: Create and connect to submodule


}
