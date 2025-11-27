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
    private PaymentLogic paymentLogic = null;
    private NavigationLogic navLogic = null;

    private CoreLogic()
    {
        if (instance != null) return;

        instance = this;

        authLogic = new AuthLogic();
        acctLogic = new AccountLogic();
        docLogic = new DocLogic();
        ticketLogic = new TicketLogic();
        paymentLogic = new PaymentLogic();
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
    public void handleViewDoc(ClientRequest req) { docLogic.handleViewDoc(req); }
    public void handleEditDoc(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleDeleteDoc(ClientRequest req) { docLogic.handleDeleteDoc(req); }

    //TicketLogic
    public void handleCreateTicket(ClientRequest req) { ticketLogic.handleCreateTicketPacket(req); }
    public void handleEditTicket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleViewTicket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleGetTicketList(ClientRequest req) { ticketLogic.handleGetTicketList(req); }

    //PaymentLogic
    public void handleRequestRent(ClientRequest req) { paymentLogic.handleRequestRent(req); }
    public void handleUpdAmountDue(ClientRequest req) { paymentLogic.handleUpdAmountDue(req); }
    public void handlePayRent(ClientRequest req) { ; } //TODO: Create and connect to submodule

    //NavigationLogic
    public void handleGetRolePacket(ClientRequest req) { navLogic.handleGetRolePacket(req); }
    public void handleTenantLandingPgInfo(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleLandlordLandingPgInfo(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleNotifications(ClientRequest req) { ; } //TODO: Create and connect to submodule



}
