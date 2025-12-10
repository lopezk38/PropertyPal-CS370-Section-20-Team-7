package com.propertypal.server.LogicBlocks;

import com.propertypal.server.ClientRequest;

import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.enums.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class TicketLogic extends BaseLogic
{
    public void handleCreateTicketPacket(ClientRequest req)
    {
        CreateTicketPacket packet = (CreateTicketPacket) req.packet;
        String title = packet.title;
        String desc = packet.description;
        int ticketType = packet.ticket_type;
        int ticketState = packet.ticket_state;

        //Validate inputs
        if (title == null || title.isEmpty() || title.length() > 50)
        {
            //Bad title
            req.setBaseErrResponse(CreateTicketResponse.CreateTicketStatus.ERR_BAD_TITLE);
            filter.sendResponse(req);
            return;
        }

        if (desc == null || desc.isEmpty() || desc.length() > 500)
        {
            //Bad description
            req.setBaseErrResponse(CreateTicketResponse.CreateTicketStatus.ERR_BAD_DESCRIPTION);
            filter.sendResponse(req);
            return;
        }

        if (!TicketEnums.Type.validate(ticketType))
        {
            //Out of range
            req.setBaseErrResponse(CreateTicketResponse.CreateTicketStatus.ERR_BAD_TICKET_TYPE);
            filter.sendResponse(req);
            return;
        }

        if (!TicketEnums.State.validate(ticketState))
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
                        title,
                        description,
                        dateCreated,
                        timeModified,
                        state,
                        type
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)""", Statement.RETURN_GENERATED_KEYS);

            ticketQ.setLong(1, userID);
            ticketQ.setLong(2, packet.lease_id);
            ticketQ.setString(3, packet.title);
            ticketQ.setString(4, packet.description);
            ticketQ.setString(5, createTime);
            ticketQ.setString(6, createTime);
            ticketQ.setInt(7, ticketState);
            ticketQ.setInt(8, ticketType);
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

                        mapQ.executeUpdate();
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
        EditTicketPacket packet = (EditTicketPacket) req.packet;
        Long ticketID = packet.ticket_id;
        Integer ticketType = packet.ticket_type;
        Integer ticketState = packet.ticket_state;
        String desc = packet.description;
        List<Long> addAtt = packet.attachment_ids;
        List<Long> remAtt = packet.remove_attachment_ids;

        if (ticketType != null)
        {
            if (!TicketEnums.Type.validate(ticketType))
            {
                //Out of range
                req.setBaseErrResponse(EditTicketResponse.EditTicketStatus.ERR_BAD_TICKET_STATE);
                filter.sendResponse(req);
                return;
            }

            PreparedStatement ticketQ = null;
            try
            {
                ticketQ = db.compileQuery("""
                        UPDATE TICKETS
                        SET type = ?
                        WHERE ticketID = ?
                        """);
                ticketQ.setInt(1, ticketType);
                ticketQ.setLong(2, ticketID);

                ticketQ.executeQuery();
            }
            catch (SQLException e)
            {
                System.out.println("ERROR: SQLException during handleEditTicketPacket ticket type query: " + e.toString());

                req.setUnknownErrResponse();
                filter.sendResponse(req);
                return;
            }
            finally
            {
                db.closeConnection(ticketQ);
            }
        }

        if (ticketState != null)
        {
            if (!TicketEnums.State.validate(ticketState))
            {
                //Out of range
                req.setBaseErrResponse(EditTicketResponse.EditTicketStatus.ERR_BAD_TICKET_STATE);
                filter.sendResponse(req);
                return;
            }

            PreparedStatement ticketQ = null;
            try
            {
                ticketQ = db.compileQuery("""
                        UPDATE TICKETS
                        SET state = ?
                        WHERE ticketID = ?
                        """);
                ticketQ.setInt(1, ticketState);
                ticketQ.setLong(2, ticketID);

                ticketQ.executeUpdate();
            }
            catch (SQLException e)
            {
                System.out.println("ERROR: SQLException during handleEditTicketPacket ticket state query: " + e.toString());

                req.setUnknownErrResponse();
                filter.sendResponse(req);
                return;
            }
            finally
            {
                db.closeConnection(ticketQ);
            }
        }

        if (desc != null)
        {
            if (desc.isEmpty() || desc.length() > 500)
            {
                //Bad description
                req.setBaseErrResponse(CreateTicketResponse.CreateTicketStatus.ERR_BAD_DESCRIPTION);
                filter.sendResponse(req);
                return;
            }

            PreparedStatement ticketQ = null;
            try
            {
                ticketQ = db.compileQuery("""
                        UPDATE TICKETS
                        SET description = ?
                        WHERE ticketID = ?
                        """);
                ticketQ.setString(1, desc);
                ticketQ.setLong(2, ticketID);

                ticketQ.executeQuery();
            }
            catch (SQLException e)
            {
                System.out.println("ERROR: SQLException during handleEditTicketPacket ticket description query: " + e.toString());

                req.setUnknownErrResponse();
                filter.sendResponse(req);
                return;
            }
            finally
            {
                db.closeConnection(ticketQ);
            }
        }

        if (addAtt != null && !addAtt.isEmpty())
        {
            for (Long attID : addAtt)
            {
                if (attID != null && attID >= 0)
                {
                    PreparedStatement ticketQ = null;
                    try
                    {
                        ticketQ = db.compileQuery("""
                                INSERT INTO TicketAttachmentsMap
                                (
                                    docID,
                                    ticketID
                                )
                                VALUES (?, ?)
                                """);
                        ticketQ.setLong(1, attID);
                        ticketQ.setLong(2, ticketID);

                        ticketQ.executeQuery();
                    }
                    catch (SQLException e)
                    {
                        System.out.println("ERROR: SQLException during handleEditTicketPacket ticket attachment add query: " + e.toString());

                        req.setUnknownErrResponse();
                        filter.sendResponse(req);
                        return;
                    }
                    finally
                    {
                        db.closeConnection(ticketQ);
                    }
                }
            }
        }

        if (remAtt != null && !remAtt.isEmpty())
        {
            for (Long attID : remAtt)
            {
                if (attID != null && attID >= 0)
                {
                    PreparedStatement ticketQ = null;
                    try
                    {
                        ticketQ = db.compileQuery("""
                                DELETE FROM TicketAttachmentsMap
                                WHERE docID = ? AND ticketID = ?
                                """);
                        ticketQ.setLong(1, attID);
                        ticketQ.setLong(2, ticketID);

                        ticketQ.executeQuery();
                    }
                    catch (SQLException e)
                    {
                        System.out.println("ERROR: SQLException during handleEditTicketPacket ticket attachment remove query: " + e.toString());

                        req.setUnknownErrResponse();
                        filter.sendResponse(req);
                        return;
                    }
                    finally
                    {
                        db.closeConnection(ticketQ);
                    }
                }
            }
        }

        //Build response and send
        req.setBaseErrResponse(BaseResponseEnum.SUCCESS);
        filter.sendResponse(req);
    }

    public void handleViewTicketPacket(ClientRequest req)
    {
        ViewTicketPacket packet = (ViewTicketPacket) req.packet;
        long ticketID = packet.ticket_id;

        //Values to retrieve
        Integer ticketType = null;
        Integer ticketState = null;
        String ticketTitle = null;
        String ticketDesc = null;
        Integer genericTicketPerms = null;
        Integer maintTicketPerms = null;
        Integer taxTicketPerms = null;
        Integer rentTicketPerms = null;
        Long parentLease = null;
        ArrayList<Long> attachments = new ArrayList<Long>();
        ArrayList<Long> comments = new ArrayList<Long>();

        //Get userID
        long userID = userIDFromToken(packet.token);
        if (userID == -1)
        {
            req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
            filter.sendResponse(req);
        }

        //Query DB for ticket data
        PreparedStatement ticketQ = null;
        try
        {
            ticketQ = db.compileQuery("""
            SELECT parentLease, title, description, state, type
            FROM Tickets
            WHERE ticketID = ?
            """);
            ticketQ.setLong(1, ticketID);
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleViewTicket ticket query compilation: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            db.closeConnection(ticketQ);
            return;
        }

        ResultSet ticketR = null;
        try
        {
            ticketR = ticketQ.executeQuery();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleViewTicket ticket query execution: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            db.closeConnection(ticketQ);
            return;
        }

        try
        {
            if (ticketR.next())
            {
                //Got a ticket
                ticketType = ticketR.getInt("type");
                ticketState = ticketR.getInt("state");
                ticketTitle = ticketR.getString("title");
                ticketDesc = ticketR.getString("description");
                parentLease = ticketR.getLong("parentLease");
            }
            else
            {
                //Ticket not found
                req.setBaseErrResponse(ViewTicketResponse.TicketStatus.ERR_BAD_TICKET);
                filter.sendResponse(req);

                db.closeConnection(ticketQ);
                return;
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleViewTicket ticket response parsing: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            db.closeConnection(ticketQ);
            return;
        }

        //Validate ticket data integrity
        if (ticketType == null || !TicketEnums.Type.validate(ticketType) || ticketState == null || !TicketEnums.State.validate(ticketState) || parentLease == null || parentLease <= 0)
        {
            //Out of range
            req.setBaseErrResponse(ViewTicketResponse.TicketStatus.ERR_BAD_TICKET);
            filter.sendResponse(req);
            return;
        }

        if (ticketTitle == null) { ticketTitle = ""; }
        if (ticketDesc == null) { ticketDesc = ""; }

        //Retrieve permissions
        PreparedStatement permQ = null;
        try
        {
            //Compile and execute query
            permQ = db.compileQuery("""
            SELECT genericTicketPerms, maintTicketPerms, taxTicketPerms, rentTicketPerms
            FROM LeasePermissions
            WHERE userID = ? AND leaseID = ?
            """);
            permQ.setLong(1, userID);
            permQ.setLong(2, parentLease);

            ResultSet permR = permQ.executeQuery();

            //Extract results
            if (permR.next())
            {
                //Got a ticket
                genericTicketPerms = permR.getInt("genericTicketPerms");
                maintTicketPerms = permR.getInt("maintTicketPerms");
                taxTicketPerms = permR.getInt("taxTicketPerms");
                rentTicketPerms = permR.getInt("rentTicketPerms");
            }

        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleViewTicket permission query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }
        finally
        {
            db.closeConnection(permQ);
        }

        //Check if user is allowed to access this ticket
        Integer relaventPerm = 0;
        switch (ticketType)
        {
            default:
            case TicketEnums.Type.STANDARD:
                relaventPerm = genericTicketPerms;
                break;

            case TicketEnums.Type.MAINTENANCE:
                relaventPerm = maintTicketPerms;
                break;

            case TicketEnums.Type.RENT:
                relaventPerm = rentTicketPerms;
                break;

            case TicketEnums.Type.TAX:
                relaventPerm = taxTicketPerms;
                break;
        }

        if ((relaventPerm & 0b001) == 0)
        {
            //User lacks permission to view
            req.setBaseErrResponse(BaseResponseEnum.ERR_PERMISSION_DENIED);
            filter.sendResponse(req);
            return;
        }

        //Get all attachments from db
        PreparedStatement attachQ = null;
        try
        {
            //Compile and execute query
            attachQ = db.compileQuery("""
            SELECT docID
            FROM TicketAttachmentsMap
            WHERE ticketID = ?
            """);
            attachQ.setLong(1, ticketID);

            ResultSet attachR = attachQ.executeQuery();

            //Extract results
            while (attachR.next())
            {
                //Got an attachment
                long curAttachID = attachR.getLong("docID");
                if (curAttachID != 0)
                {
                    attachments.add(curAttachID);
                }
                else
                {
                    System.out.println("WARNING: Got null attachment for ticket: " + Long.toString(ticketID));
                }
            }

        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleViewTicket attachment query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }
        finally
        {
            db.closeConnection(attachQ);
        }
        if (attachments.isEmpty()) { attachments = null; }

        //Get all comments from db
        PreparedStatement commentQ = null;
        try
        {
            //Compile and execute query
            commentQ = db.compileQuery("""
            SELECT commentID
            FROM TicketCommentsMap
            WHERE ticketID = ?
            """);
            commentQ.setLong(1, ticketID);

            ResultSet commentR = commentQ.executeQuery();

            //Extract results
            while (commentR.next())
            {
                //Got an attachment
                long curComID = commentR.getLong("commentID");
                if (curComID != 0)
                {
                    comments.add(curComID);
                }
                else
                {
                    System.out.println("WARNING: Got null attachment for ticket: " + Long.toString(ticketID));
                }
            }

        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleViewTicket attachment query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }
        finally
        {
            db.closeConnection(commentQ);
        }
        if (comments.isEmpty()) { comments = null; }

        //Build response and send
        ViewTicketResponse resp = new ViewTicketResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        resp.TICKET_TYPE = ticketType;
        resp.TICKET_STATE = ticketState;
        resp.TITLE = ticketTitle;
        resp.DESCRIPTION = ticketDesc;
        resp.PERMISSIONS = relaventPerm;
        resp.ATTACHMENT_IDS = attachments;
        resp.COMMENT_IDS = comments;
        req.setResponse(resp);
        filter.sendResponse(req);
    }

    public void handleGetTicketList(ClientRequest req)
    {
        GetTicketListPacket packet = (GetTicketListPacket) req.packet;
        long parentLease = packet.lease_id;

        //Values to retrieve
        ArrayList<Long> tickets = new ArrayList<Long>();

        //Get userID
        long userID = userIDFromToken(packet.token);
        if (userID == -1)
        {
            req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
            filter.sendResponse(req);
        }

        //Query DB for ticket list
        PreparedStatement ticketsQ = null;
        try
        {
            ticketsQ = db.compileQuery("""
            SELECT ticketID
            FROM Tickets
            WHERE parentLease = ?
            """);
            ticketsQ.setLong(1, parentLease);
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleGetTicketList ticket query compilation: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }

        ResultSet ticketsR = null;
        try
        {
            ticketsR = ticketsQ.executeQuery();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleGetTicketList ticket query execution: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            db.closeConnection(ticketsQ);
            return;
        }

        //TODO check if user is allowed to access each ticket

        //TODO add response for ERR_BAD_LEASE somewhere

        try
        {
            while (ticketsR.next())
            {
                //adds ticketID to tickets list
                tickets.add(ticketsR.getLong("ticketID"));
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during getTicketList ticket response parsing: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            db.closeConnection(ticketsQ);
            return;
        }
        finally
        {
            db.closeConnection(ticketsQ);
        }
        if (tickets.isEmpty()) { tickets = null; }

        //Build response and send
        GetTicketListResponse resp = new GetTicketListResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        resp.TICKETS = tickets;

        req.setResponse(resp);
        filter.sendResponse(req);
    }

    public void handleGetTicketInfo(ClientRequest req)
    {
        GetTicketInfoPacket packet = (GetTicketInfoPacket) req.packet;
        Long ticketID = packet.ticket_id;

        if (ticketID == null || ticketID < 1)
        {
            req.setBaseErrResponse(GetTicketInfoResponse.GetTicketInfoStatus.ERR_BAD_TICKET);
            filter.sendResponse(req);

            return;
        }

        //Query DB for info
        PreparedStatement ticketQ = null;
        String title = null;
        String desc = null;
        LocalDateTime lastUpdTime = null;
        Integer state = null;
        Integer type = null;
        try
        {
            //Compile and execute query
            ticketQ = db.compileQuery("""
            SELECT title, description, timeModified, state, type
            FROM Tickets
            WHERE ticketID = ?
            """);
            ticketQ.setLong(1, ticketID);

            ResultSet ticketR = ticketQ.executeQuery();

            //Extract results
            if (ticketR.next())
            {
                //Got data for this ticket ID
                title = ticketR.getString("title");
                desc = ticketR.getString("description");
                lastUpdTime = ticketR.getTimestamp("timeModified").toLocalDateTime();
                state = ticketR.getInt("state");
                type = ticketR.getInt("type");
            }
            else
            {
                //Ticket ID was not in DB
                req.setBaseErrResponse(GetTicketInfoResponse.GetTicketInfoStatus.ERR_BAD_TICKET);
                filter.sendResponse(req);

                return;
            }

        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleGetTicketInfo ticket query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }
        finally
        {
            db.closeConnection(ticketQ);
        }


        //Return the data to the client
        GetTicketInfoResponse resp = new GetTicketInfoResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        resp.TITLE = title;
        resp.DESCRIPTION = desc;
        resp.STATE = state;
        resp.TYPE = type;
        resp.LAST_UPDATED = lastUpdTime;
        req.setResponse(resp);
        req.sendResponse();

        return;
    }
}