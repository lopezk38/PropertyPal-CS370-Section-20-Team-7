package com.propertypal.filters;

import com.propertypal.ClientRequest;
import com.propertypal.network.packets.*;
import com.propertypal.network.responses.*;
import com.propertypal.SecurityFilter;

public class TicketFilters extends BaseFilters
{
    public void filterCreateTicketPacket(ClientRequest req)
    {
        if (!(req.packet instanceof CreateTicketPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: CreateTicketPacket is registered to the wrong endpoint");
            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        //Verify user is a part of this lease
        //TODO

        //All tests passed, let it through
        logic.handleCreateTicket(req);
    }

    public void filterEditTicketPacket(ClientRequest req)
    {
        ;
    }

    public void filterViewTicketPacket(ClientRequest req)
    {
        ;
    }

}