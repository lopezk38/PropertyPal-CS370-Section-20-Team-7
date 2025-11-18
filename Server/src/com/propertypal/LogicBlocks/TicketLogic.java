package com.propertypal.LogicBlocks;

import com.propertypal.ClientRequest;
import com.propertypal.network.responses.*;
import com.propertypal.network.packets.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public class TicketLogic extends BaseLogic
{
    public void handleCreateTicketPacket(ClientRequest req)
    {
        CreateTicketPacket packet = (CreateTicketPacket) req.packet;
        int ticketType = packet.ticket_type;
        int ticketState = packet.ticket_state;

        //Validate inputs
        if (ticketType < TicketEnums.Type.STANDARD || ticketType > TicketEnums.Type.RENT)
        {
            //Out of range
            req.setBaseErrResponse(CreateTicketResponse.CreateTicketStatus.ERR_BAD_TICKET_TYPE);
            filter.sendResponse(req);
            return;
        }

        if (ticketState < TicketEnums.State.NEW || ticketState > TicketEnums.State.CLOSED)
        {
            //Out of range
            req.setBaseErrResponse(CreateTicketResponse.CreateTicketStatus.ERR_BAD_TICKET_STATE);
            filter.sendResponse(req);
            return;
        }

        //Get userID
        long userID = userIDFromToken(packet.token);
        if (userID == -1)
        {
            req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
            filter.sendResponse(req);
        }

        //Create ticket entry in DB, keep ID around
        PreparedStatement ticketQ = null;
        long ticketID;
        String createTime = LocalDateTime.now().toString();
        try
        {
            //Create new ticket entry
            ticketQ = db.compileQuery("""
                    INSERT INTO Tickets (
                        owner,
                        parentLease,
                        description,
                        dateCreated,
                        timeModified,
                        status
                    )
                    VALUES (?, ?, ?, ?, ?)""", Statement.RETURN_GENERATED_KEYS);

            ticketQ.setLong(1, userID);
            ticketQ.setString(2, packet.lease_id);
            ticketQ.setString(3, packet.description);
            ticketQ.setString(4, createTime);
            ticketQ.setString(5, createTime);
            ticketQ.setInt(6, ticketState);
            ticketQ.executeUpdate();

            //Get the newly made primary key
            ResultSet res = ticketQ.getGeneratedKeys();
            if (res.next())
            {
                ticketID = res.getLong(1);
            }
            else throw new SQLException("Failed to generate ticket primary key");
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleCreateTicketPacket DB insert: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }
        finally
        {
            db.closeConnection(ticketQ);
        }

        //Create TAM entries as needed
        if (packet.attachment_ids != null)
        {
            PreparedStatement mapQ = null;
            try
            {
                for (Long docID : packet.attachment_ids)
                {
                    if (docID != null)
                    {
                        //Create new TAM entry
                        mapQ = db.compileQuery("""
                                INSERT INTO TicketAttachmentsMap (
                                    docID,
                                    ticketID
                                )
                                VALUES (?, ?)""");

                        mapQ.setLong(1, docID);
                        mapQ.setLong(2, ticketID);
                    }
                    else { System.out.println("WARNING: Got null attachment for ticket. Ignoring..."); }
                }
            }
            catch (SQLException e)
            {
                System.out.println("ERROR: SQLException during TicketAttachmentMap DB insert: " + e.toString());

                req.setUnknownErrResponse();
                filter.sendResponse(req);

                return;
            }
            finally
            {
                db.closeConnection(mapQ);
            }
        }

        //Respond with OK
        CreateTicketResponse resp = new CreateTicketResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        resp.TICKET_ID = ticketID;
        req.setResponse(resp);
        filter.sendResponse(req);

        return;
    }

    public void handleEditTicketPacket(ClientRequest req)
    {
        ;
    }

    public void handleViewTicketPacket(ClientRequest req)
    {
        ;
    }
}