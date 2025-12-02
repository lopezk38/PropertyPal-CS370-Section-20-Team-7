package com.propertypal;

import com.propertypal.filters.*;
import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.packets.*;

public class SecurityFilter
{
    private static SecurityFilter instance = null;

    private AuthFilters authFilter = null;
    private AccountFilters acctFilter = null;
    private TicketFilters ticketFilter = null;
    private PaymentFilters paymentFilter = null;
    private DocFilters docFilter = null;
    private NavigationFilters navFilter = null;

    private SecurityFilter()
    {
        if (instance != null) return;

        instance = this;

        authFilter = new AuthFilters();
        acctFilter = new AccountFilters();
        ticketFilter = new TicketFilters();
        paymentFilter = new PaymentFilters();
        docFilter = new DocFilters();
        navFilter = new NavigationFilters();
    }

    public static SecurityFilter getInstance()
    {
        if (instance == null)
        {
            instance = new SecurityFilter();
        }

        return instance;
    }

    public int enforceLoggedIn(ClientRequest req) { return authFilter.enforceLoggedIn(req); }

    //AuthFilters
    public void filterLoginPacket(ClientRequest req) { authFilter.filterLoginPacket(req); }
    public void filterLogoutPacket(ClientRequest req) { authFilter.filterLogoutPacket(req); }

    //AccountFilters
    public void filterCreateTenantAcctPacket(ClientRequest req) { acctFilter.filterCreateTenantAcctPacket(req); }
    public void filterCreateLandlordAcctPacket(ClientRequest req) { acctFilter.filterCreateLandlordAcctPacket(req); }
    public void filterCreateInvitePacket(ClientRequest req) { acctFilter.filterCreateInvitePacket(req); }
    public void filterAcceptInvitePacket(ClientRequest req) { acctFilter.filterAcceptInvitePacket(req); }
    public void filterGetInviteListPacket(ClientRequest req) { acctFilter.filterGetInviteListPacket(req); }
    public void filterGetAcctLeasePacket(ClientRequest req) { acctFilter.filterGetAcctLeasePacket(req); }

    //DocFilters
    public void filterUploadDocPacket(ClientRequest req) { docFilter.filterUploadDocPacket(req); }
    public void filterViewDocPacket(ClientRequest req) { docFilter.filterViewDocPacket(req); }
    public void filterEditDocPacket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void filterDeleteDocPacket(ClientRequest req) { docFilter.filterDeleteDocPacket(req); }
    public void filterGetDocInfoPacket(ClientRequest req) { docFilter.filterGetDocInfoPacket(req); }
    public void filterGetDocListPacket(ClientRequest req) { docFilter.filterGetDocListPacket(req); }

    //TicketFilters
    public void filterCreateTicketPacket(ClientRequest req) { ticketFilter.filterCreateTicketPacket(req); }
    public void filterEditTicketPacket(ClientRequest req) { ticketFilter.filterEditTicketPacket(req); }
    public void filterViewTicketPacket(ClientRequest req) { ticketFilter.filterViewTicketPacket(req);}
    public void filterGetTicketListPacket(ClientRequest req) { ticketFilter.filterGetTicketList(req);}
    public void filterGetTicketInfoPacket(ClientRequest req) { ticketFilter.filterGetTicketInfoPacket(req); }

    //PaymentFilters
    public void filterRequestRentPacket(ClientRequest req) { paymentFilter.filterRequestRentPacket(req); }
    public void filterUpdAmountDuePacket(ClientRequest req) { paymentFilter.filterUpdAmountDuePacket(req); }
    public void filterGetPayLinkPacket(ClientRequest req) { paymentFilter.filterGetPayLinkPacket(req); }

    //NavigationFilters
    public void filterGetRolePacket(ClientRequest req) { navFilter.filterGetRolePacket(req); }
    public void filterGetLeaseContactsPacket(ClientRequest req) { navFilter.filterGetLeaseContactsPacket(req); }
    public void filterTenantLandingPgInfoPacket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void filterLandlordLandingPgInfoPacket(ClientRequest req) { ; } //TODO: Create and connect to submodule
    public void filterNotificationsPacket(ClientRequest req) { ; } //TODO: Create and connect to submodule

    //Outbound
    public void sendResponse(ClientRequest request) { request.sendResponse(); }



}
