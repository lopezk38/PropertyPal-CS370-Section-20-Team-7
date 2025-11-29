package com.propertypal.client.ClientLogic;

import com.propertypal.shared.network.packets.*;
import com.propertypal.client.APIHandler;
import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.enums.TicketEnums;

import java.io.IOException; //for throwing IOException
import java.util.List; //for sending & receive lists (like ticket ID)

public class TicketLogic {
    APIHandler handler = null;

    public TicketLogic() {
        handler = APIHandler.getInstance(); //get shared APIHandler instance for sending network requests
    }

    //create new ticket (called by TicketCreateController)
    public CreateTicketResponse createticket(long leaseID, String description) throws IOException {
        CreateTicketPacket tktpkt = new CreateTicketPacket();

        tktpkt.lease_id = leaseID; //tells server which lease the ticket belongs to
        tktpkt.ticket_type = TicketEnums.Type.MAINTENANCE; //set type of the ticket to maintenance
        tktpkt.ticket_state = TicketEnums.State.NEW; //new tickets start in NEW state
        tktpkt.description = description; //description of ticket from user
        tktpkt.attachment_ids = List.of(); //no attachments

        CreateTicketResponse resp = handler.sendRequest("/ticket/create", tktpkt, CreateTicketResponse.class);

        if (resp.STATUS != 0) //if server rejects request
        {
            throw new IOException("CreateTicket failed. STATUS = " + resp.STATUS);
        }

        return resp; //return server's response
    }
}
