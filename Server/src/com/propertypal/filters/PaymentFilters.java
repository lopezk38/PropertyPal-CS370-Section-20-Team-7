package com.propertypal.filters;

import com.propertypal.ClientRequest;
import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.enums.*;

import java.lang.ref.Cleaner;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class PaymentFilters extends BaseFilters
{
    public void filterRequestRentPacket(ClientRequest req)
    {
        if (!(req.packet instanceof RequestRentPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterRequestRentPacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        //Validate user is owner of property/lease
        if (isUserOwner(req) != BaseResponseEnum.SUCCESS) return;

        //All checks passed, let it through
        logic.handleRequestRent(req);
    }

    public void filterUpdAmountDuePacket(ClientRequest req)
    {
        if (!(req.packet instanceof UpdateAmountDuePacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterUpdAmountDuePacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        //Validate user is owner of property/lease
        if (isUserOwner(req) != BaseResponseEnum.SUCCESS) return;

        //All checks passed, let it through
        logic.handleUpdAmountDue(req);
    }

    public void filterGetPayLinkPacket(ClientRequest req)
    {
        if (!(req.packet instanceof GetPayLinkPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterGetPayLinkPacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        //TODO Make sure user is tenant to lease

        //All checks passed, forward packet
        logic.handleGetPayLinkPacket(req);
    }

    private int isUserOwner(ClientRequest req)
    {
        String token = req.packet.token;

        //Make sure user is landlord and owns this lease
        //Check if user is a landlord
        PreparedStatement userQ = null;
        Long userID = null;
        Boolean isLandlord = null;
        try
        {
            userQ = db.compileQuery("""
                    SELECT userID, isLandlord
                    FROM Users
                    WHERE loginAuthToken = ?
                    """);

            userQ.setString(1, token);

            ResultSet userR = userQ.executeQuery();

            if (userR.next())
            {
                userID = userR.getLong("userID");
                isLandlord = userR.getBoolean("isLandlord");
            }
            else
            {
                //Token wasn't in the DB
                req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
                filter.sendResponse(req);

                return BaseResponseEnum.ERR_BAD_TOKEN;
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during filterRequestRentPacket user query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return BaseResponseEnum.ERR_UNKNOWN;
        }
        finally
        {
            db.closeConnection(userQ);
        }

        if (userID == null)
        {
            //Token did not have an attached userID somehow?
            req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
            filter.sendResponse(req);

            return BaseResponseEnum.ERR_BAD_TOKEN;
        }

        if (isLandlord == null || isLandlord == false)
        {
            //Not a landlord, can't request rent
            req.setBaseErrResponse(BaseResponseEnum.ERR_PERMISSION_DENIED);
            filter.sendResponse(req);

            return BaseResponseEnum.ERR_PERMISSION_DENIED;
        }

        //Retrieve property lease is under
        Long leaseID = ((RequestRentPacket) req.packet).lease_id;
        PreparedStatement leaseQ = null;
        Long propID = null;
        try
        {
            leaseQ = db.compileQuery("""
                    SELECT associatedProperty
                    FROM Leases
                    WHERE leaseID = ?
                    """);

            leaseQ.setLong(1, leaseID);

            ResultSet leaseR = leaseQ.executeQuery();

            if (leaseR.next())
            {
                propID = leaseR.getLong("associatedProperty");
            }
            else
            {
                //Lease wasn't in the DB
                req.setBaseErrResponse(RequestRentResponse.RequestRentStatus.ERR_BAD_LEASE);
                filter.sendResponse(req);

                return RequestRentResponse.RequestRentStatus.ERR_BAD_LEASE;
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during filterRequestRentPacket lease query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return BaseResponseEnum.ERR_UNKNOWN;
        }
        finally
        {
            db.closeConnection(leaseQ);
        }

        if (propID == null)
        {
            //Lease is invalid, has no property attached
            req.setBaseErrResponse(RequestRentResponse.RequestRentStatus.ERR_BAD_LEASE);
            filter.sendResponse(req);

            return RequestRentResponse.RequestRentStatus.ERR_BAD_LEASE;
        }

        //Retrieve owner of that property
        PreparedStatement propQ = null;
        Long ownerID = null;
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
                ownerID = propR.getLong("owner");
            }
            else
            {
                //Property wasn't in the DB, malformed lease
                req.setBaseErrResponse(RequestRentResponse.RequestRentStatus.ERR_BAD_LEASE);
                filter.sendResponse(req);

                return RequestRentResponse.RequestRentStatus.ERR_BAD_LEASE;
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during filterRequestRentPacket property query: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return BaseResponseEnum.ERR_UNKNOWN;
        }
        finally
        {
            db.closeConnection(propQ);
        }

        if (ownerID == null || ownerID != userID)
        {
            //User is not the owner of the property, reject
            req.setBaseErrResponse(BaseResponseEnum.ERR_PERMISSION_DENIED);
            filter.sendResponse(req);

            return BaseResponseEnum.ERR_PERMISSION_DENIED;
        }

        //If we got here, they're the owner
        return BaseResponseEnum.SUCCESS;
    }
}
