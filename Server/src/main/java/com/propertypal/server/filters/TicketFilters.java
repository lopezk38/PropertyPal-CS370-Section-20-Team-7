package com.propertypal.server.filters;

import com.propertypal.server.ClientRequest;
import com.propertypal.server.SecurityFilter;

import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.enums.*;

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

        //TODO Verify user is allowed to create

        //All tests passed, let it through
        logic.handleCreateTicket(req);
    }

    public void filterEditTicketPacket(ClientRequest req)
    {
        if (!(req.packet instanceof EditTicketPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: EditTicketPacket is registered to the wrong endpoint");
            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        //TODO Verify user is allowed to edit

        //All tests passed, let it through
        logic.handleEditTicket(req);
    }

    public void filterViewTicketPacket(ClientRequest req)
    {
        if (!(req.packet instanceof ViewTicketPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: ViewTicketPacket is registered to the wrong endpoint");
            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        //TODO Verify user is allowed to view

        //All tests passed, let it through
        logic.handleViewTicket(req);
    }

    public void filterGetTicketList(ClientRequest req)
    {
        if (!(req.packet instanceof GetTicketListPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: GetTicketListPacket is registered to the wrong endpoint");
            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        //TODO Verify user is allowed to ask for tickets on this lease

        //All tests passed, let it through
        logic.handleGetTicketList(req);

    }

    public void filterGetTicketInfoPacket(ClientRequest req)
    {
        if (!(req.packet instanceof GetTicketInfoPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterGetTicketInfoPacket is registered to the wrong endpoint");
            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        //TODO Verify user is allowed to ask for tickets on this lease

        //All tests passed, let it through
        logic.handleGetTicketInfo(req);
    }
}