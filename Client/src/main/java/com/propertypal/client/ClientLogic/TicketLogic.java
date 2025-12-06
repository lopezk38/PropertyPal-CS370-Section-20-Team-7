package com.propertypal.client.ClientLogic;

import com.propertypal.client.APIHandler;
import com.propertypal.shared.network.enums.TicketEnums;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.responses.*;

import java.io.IOException; //for throwing IOException
import java.util.ArrayList;
import java.util.List; //for sending & receive lists (like ticket ID)

public class TicketLogic
{
    private static TicketLogic instance = null;
    private APIHandler handler = null;

    private TicketLogic()
    {
        handler = APIHandler.getInstance(); //get shared APIHandler instance for sending network requests
    }

    public static TicketLogic getInstance()
    {
        if (instance == null)
        {
            instance = new TicketLogic();
        }

        return instance;
    }

    //create new ticket (called by TicketCreateController)
    public CreateTicketResponse createTicket(long leaseID, String title, String description) throws IOException
    {
        CreateTicketPacket tktpkt = new CreateTicketPacket();

        tktpkt.lease_id = leaseID; //tells server which lease the ticket belongs to
        tktpkt.ticket_type = TicketEnums.Type.MAINTENANCE; //set type of the ticket to maintenance
        tktpkt.ticket_state = TicketEnums.State.NEW; //new tickets start in NEW state
        tktpkt.title = title;
        tktpkt.description = description; //description of ticket from user
        tktpkt.attachment_ids = List.of(); //no attachments

        CreateTicketResponse resp = handler.sendRequest("/ticket/new", tktpkt, CreateTicketResponse.class);

        if (resp.STATUS != 0) //if server rejects request
        {
            throw new IOException("CreateTicket failed. STATUS = " + resp.STATUS);
        }

        return resp; //return server's response
    }

    //get ticket list
    public ArrayList<Long> getTicketIDList(long leaseID) throws IOException
    {
        GetTicketListPacket tktpkt = new GetTicketListPacket();
        tktpkt.lease_id = leaseID;

        GetTicketListResponse resp = handler.sendRequest("/ticket/list", tktpkt, GetTicketListResponse.class);

        if (resp.STATUS != 0)
        {
            throw new IOException("GetTicketList failed. STATUS = " + resp.STATUS);
        }

        return resp.TICKETS;

    }

    //get ticket info
    public GetTicketInfoResponse getTicketInfo(long ticketID) throws IOException
    {
        GetTicketInfoPacket tktpkt = new GetTicketInfoPacket();
        tktpkt.ticket_id = ticketID;

        GetTicketInfoResponse resp = handler.sendRequest("/ticket/info", tktpkt, GetTicketInfoResponse.class);

        if (resp.STATUS != 0)
        {
            throw new IOException("GetTicketInfo failed. STATUS = " + resp.STATUS);
        }

        return resp;

    }

    //close ticket
    public void closeTicket(long ticketID) throws IOException
    {
        EditTicketPacket p = new EditTicketPacket();
        p.ticket_id = ticketID;

        p.ticket_state = TicketEnums.State.CLOSED; //mark ticket closed
        p.ticket_type = null; //no type change
        p.description = null; //no description change
        p.attachment_ids = null;
        p.remove_attachment_ids = null;

        EditTicketResponse resp = handler.sendRequest("/ticket/edit", p, EditTicketResponse.class);

        if (resp.STATUS != 0)
        {
            throw new IOException("CloseTicket failed. STATUS=" + resp.STATUS);
        }
    }
}