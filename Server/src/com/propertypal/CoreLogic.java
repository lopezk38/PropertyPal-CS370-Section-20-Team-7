package com.propertypal;

import com.propertypal.LogicBlocks.AccountLogic;
import com.propertypal.logic.*;

public class CoreLogic
{
    private static CoreLogic instance = null;

    private AuthLogic authLogic = new AuthLogic();
    private AccountLogic acctLogic = new AccountLogic();

    private CoreLogic()
    {
        if (instance != null) return;

        instance = this;
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
    public void handleCreateAcct(ClientRequest req) { acctLogic.handleCreateAcct(req); } //TODO: Create and connect to submodule
    public void handleCreateInvite(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleUploadDoc(ClientRequest req) { ; } //TODO: Create and connect to submodule

    //DocLogic
    public void handleViewDoc(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleEditDoc(ClientRequest req) { ; } //TODO: Create and connect to submodule

    //TicketLogic
    public void handleCreateTicket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleEditTicket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleViewTicket(ClientRequest req) { ; } //TODO: Create and connect to submodule

    //PaymentLogic
    public void handleRequestRent(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleUpdAmountDue(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handlePayRent(ClientRequest req) { ; } //TODO: Create and connect to submodule

    //NavigationLogic
    public void handleTenantLandingPgInfo(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleLandlordLandingPgInfo(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void handleNotifications(ClientRequest req) { ; } //TODO: Create and connect to submodule

}
