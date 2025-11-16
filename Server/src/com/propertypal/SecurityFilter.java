package com.propertypal;

import com.propertypal.filters.AccountFilters;
import com.propertypal.filters.AuthFilters;
import com.propertypal.network.responses.*;
import com.propertypal.network.packets.*;

public class SecurityFilter
{
    private static SecurityFilter instance = null;

    private AuthFilters authFilter = new AuthFilters();
    private AccountFilters acctFilter = new AccountFilters();

    private SecurityFilter()
    {
        if (instance != null) return;

        instance = this;
    }

    public static SecurityFilter getInstance()
    {
        if (instance == null)
        {
            instance = new SecurityFilter();
        }

        return instance;
    }

    private int enforceLoggedIn(ClientRequest req) { return authFilter.enforceLoggedIn(req); }

    //AuthFilters
    public void filterLoginPacket(ClientRequest req) { authFilter.filterLoginPacket(req); }
    public void filterLogoutPacket(ClientRequest req) { authFilter.filterLogoutPacket(req); }

    //AccountFilters
    public void filterCreateTenantAcctPacket(ClientRequest req) { authFilter.filterCreateTenantAcctPacket(req); }
    public void filterCreateLandlordAcctPacket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void filterCreateInvitePacket(ClientRequest req) { acctFilter.filterCreateInvitePacket(req); }
    public void filterAcceptInvitePacket(ClientRequest req) { acctFilter.filterAcceptInvitePacket(req); }

    //DocFilters
    public void filterUploadDocPacket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void filterViewDocPacket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void filterEditDocPacket(ClientRequest req) { ; } //TODO: Create and connect to submodule

    //TicketFilters
    public void filterCreateTicketPacket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void filterEditTicketPacket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void filterViewTicketPacket(ClientRequest req) { ; }  //TODO: Create and connect to submodule

    //PaymentFilters
    public void filterRequestRentPacket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void filterUpdAmountDuePacket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void filterPayRentPacket(ClientRequest req) { ; } //TODO: Create and connect to submodule

    //NavigationFilters
    public void filterTenantLandingPgInfoPacket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void filterLandlordLandingPgInfoPacket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void filterNotificationsPacket(ClientRequest req) { ; } //TODO: Create and connect to submodule

    //Outbound
    public void sendResponse(ClientRequest request) { request.sendResponse(); }
}
