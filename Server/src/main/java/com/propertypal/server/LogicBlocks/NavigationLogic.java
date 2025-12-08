package com.propertypal.server.LogicBlocks;

import com.propertypal.server.ClientRequest;

import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.enums.*;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class NavigationLogic extends BaseLogic
{
    public void handleGetRolePacket(ClientRequest req)
    {
        GetRolePacket packet = (GetRolePacket) req.packet;
        Long leaseID = packet.lease_id;
        long userID = userIDFromToken(packet.token);

        if (userID < 1)
        {
            //Bad ID somehow
            req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
            req.sendResponse();

            return;
        }

        if (leaseID == null || leaseID < 1)
        {
            //Bad lease
            GetRoleResponse resp = new GetRoleResponse();
            resp.STATUS = GetRoleResponse.GetRoleStatus.ERR_BAD_LEASE;
            req.setResponse(resp);
            req.sendResponse();

            return;
        }

        //Query lease table for tenant and property IDs
        PreparedStatement leaseQ = null;
        Long propID = null;
        Long tenantID = null;
        try
        {
            leaseQ = db.compileQuery("""
                SELECT associatedProperty, tenantID
                FROM Leases
                WHERE leaseID = ?
                """);

            leaseQ.setLong(1, leaseID);

            ResultSet leaseR = leaseQ.executeQuery();

            if (leaseR.next())
            {
                propID = leaseR.getLong("associatedProperty");
                tenantID = leaseR.getLong("tenantID");
            }
            else
            {
                //Lease didn't exist in DB
                GetRoleResponse resp = new GetRoleResponse();
                resp.STATUS = GetRoleResponse.GetRoleStatus.ERR_BAD_LEASE;
                req.setResponse(resp);
                req.sendResponse();

                return;
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleGetRolePacket lease query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }
        finally
        {
            db.closeConnection(leaseQ);
        }

        if (tenantID == userID)
        {
            //User is tenant
            GetRoleResponse resp = new GetRoleResponse();
            resp.STATUS = BaseResponseEnum.SUCCESS;
            resp.ROLE = RoleEnum.Role.TENANT;
            req.setResponse(resp);
            req.sendResponse();

            return;
        }

        //At this point, user must be landlord or not involved in this lease. Landlord is identified by owning the prop
        if (propID == null || propID < 1)
        {
            //Somehow lease has no property?
            System.out.println("ERROR: Lease has no associated property!");

            req.setUnknownErrResponse();
            req.sendResponse();

            return;
        }

        //Dereference property
        PreparedStatement propQ = null;
        Long landlordID = null;
        try
        {
            propQ = db.compileQuery("""
                SELECT owner
                FROM Properties
                WHERE propertyID = ?
                """);

            propQ.setLong(1, propID);

            ResultSet propR = propQ.executeQuery();

            if (propR.next())
            {
                landlordID = propR.getLong("owner");
            }
            else
            {
                //Property didn't exist in DB
                GetRoleResponse resp = new GetRoleResponse();
                resp.STATUS = GetRoleResponse.GetRoleStatus.ERR_BAD_LEASE;
                req.setResponse(resp);
                req.sendResponse();

                return;
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleGetRolePacket property query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }
        finally
        {
            db.closeConnection(propQ);
        }

        if (landlordID == userID)
        {
            //User is landlord
            GetRoleResponse resp = new GetRoleResponse();
            resp.STATUS = BaseResponseEnum.SUCCESS;
            resp.ROLE = RoleEnum.Role.LANDLORD;
            req.setResponse(resp);
            req.sendResponse();

            return;
        }

        //If we got here, user is not a tenant or a landlord. Future version would check for other role types, but not in this version
        GetRoleResponse resp = new GetRoleResponse();
        resp.STATUS = GetRoleResponse.GetRoleStatus.ERR_NOT_PARTICIPANT;
        req.setResponse(resp);
        req.sendResponse();

        return;
    }

    public void handleGetLeaseContactsPacket(ClientRequest req)
    {
        GetLeaseContactsPacket packet = (GetLeaseContactsPacket) req.packet;
        Long leaseID = packet.lease_id;
        long userID = userIDFromToken(packet.token);

        //Data needed for response
        String llFname;
        String llLname;
        String llEmail;
        String llPhone;
        String ttFname;
        String ttLname;
        String ttEmail;
        String ttPhone;

        if (userID < 1)
        {
            //Bad ID somehow
            req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
            req.sendResponse();

            return;
        }

        if (leaseID == null || leaseID < 1)
        {
            //Bad lease
            GetLeaseContactsResponse resp = new GetLeaseContactsResponse();
            resp.STATUS = GetLeaseContactsResponse.GetLeaseContactsStatus.ERR_BAD_LEASE;
            req.setResponse(resp);
            req.sendResponse();

            return;
        }

        //Query lease table for tenant and property IDs
        PreparedStatement leaseQ = null;
        Long propID = null;
        Long tenantID = null;
        try
        {
            leaseQ = db.compileQuery("""
                SELECT associatedProperty, tenantID
                FROM Leases
                WHERE leaseID = ?
                """);

            leaseQ.setLong(1, leaseID);

            ResultSet leaseR = leaseQ.executeQuery();

            if (leaseR.next())
            {
                propID = leaseR.getLong("associatedProperty");
                tenantID = leaseR.getLong("tenantID");
            }
            else
            {
                //Lease didn't exist in DB
                GetLeaseContactsResponse resp = new GetLeaseContactsResponse();
                resp.STATUS = GetLeaseContactsResponse.GetLeaseContactsStatus.ERR_BAD_LEASE;
                req.setResponse(resp);
                req.sendResponse();

                return;
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleGetLeaseContactsPacket lease query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }
        finally
        {
            db.closeConnection(leaseQ);
        }

        //At this point, user must be landlord or not involved in this lease. Landlord is identified by owning the prop
        if (propID == null || propID < 1)
        {
            //Somehow lease has no property?
            System.out.println("ERROR: Lease has no associated property!");

            req.setUnknownErrResponse();
            req.sendResponse();

            return;
        }

        //Dereference property
        PreparedStatement propQ = null;
        Long landlordID = null;
        try
        {
            propQ = db.compileQuery("""
                SELECT owner
                FROM Properties
                WHERE propertyID = ?
                """);

            propQ.setLong(1, propID);

            ResultSet propR = propQ.executeQuery();

            if (propR.next())
            {
                landlordID = propR.getLong("owner");
            }
            else
            {
                //Property didn't exist in DB
                GetLeaseContactsResponse resp = new GetLeaseContactsResponse();
                resp.STATUS = GetLeaseContactsResponse.GetLeaseContactsStatus.ERR_BAD_LEASE;
                req.setResponse(resp);
                req.sendResponse();

                return;
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleGetLeaseContactsPacket property query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }
        finally
        {
            db.closeConnection(propQ);
        }

        //Check if user is on the lease
        if (userID != landlordID && userID != tenantID)
        {
            GetLeaseContactsResponse resp = new GetLeaseContactsResponse();
            resp.STATUS = GetLeaseContactsResponse.GetLeaseContactsStatus.ERR_NOT_PARTICIPANT;
            req.setResponse(resp);
            req.sendResponse();
        }

        //Query for landlord and tenant's info
        //Dereference property
        PreparedStatement llQ = null;
        PreparedStatement ttQ = null;
        try
        {
            llQ = db.compileQuery("""
                SELECT firstName, lastName, email, phone
                FROM Users
                WHERE userID = ?
                """);

            llQ.setLong(1, landlordID);

            ResultSet llR = llQ.executeQuery();

            if (llR.next())
            {
                llFname = llR.getString("firstName");
                llLname = llR.getString("lastName");
                llEmail = llR.getString("email");
                llPhone = llR.getString("phone");
            }
            else
            {
                //UserID didn't exist in DB somehow
                req.setUnknownErrResponse();
                req.sendResponse();

                return;
            }

            ttQ = db.compileQuery("""
                SELECT firstName, lastName, email, phone
                FROM Users
                WHERE userID = ?
                """);

            ttQ.setLong(1, tenantID);

            ResultSet ttR = ttQ.executeQuery();

            if (ttR.next())
            {
                ttFname = ttR.getString("firstName");
                ttLname = ttR.getString("lastName");
                ttEmail = ttR.getString("email");
                ttPhone = ttR.getString("phone");
            }
            else
            {
                //UserID didn't exist in DB somehow
                req.setUnknownErrResponse();
                req.sendResponse();

                return;
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleGetLeaseContactsPacket users query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }
        finally
        {
            db.closeConnection(llQ);
            db.closeConnection(ttQ);
        }

        //All good, reply
        GetLeaseContactsResponse resp = new GetLeaseContactsResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        resp.LL_FNAME = llFname;
        resp.LL_LNAME = llLname;
        resp.LL_EMAIL = llEmail;
        resp.LL_PHONE = llPhone;
        resp.TT_FNAME = ttFname;
        resp.TT_LNAME = ttLname;
        resp.TT_EMAIL = ttEmail;
        resp.TT_PHONE = ttPhone;
        req.setResponse(resp);
        req.sendResponse();

        return;
    }
}
