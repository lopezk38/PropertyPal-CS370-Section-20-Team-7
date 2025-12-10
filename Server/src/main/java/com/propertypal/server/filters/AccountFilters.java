package com.propertypal.server.filters;

import com.propertypal.server.ClientRequest;
import com.propertypal.server.CoreLogic;
import com.propertypal.server.DbWrapper;
import com.propertypal.server.SecurityFilter;
import com.propertypal.server.LogicBlocks.AccountLogic;

import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.enums.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountFilters extends BaseFilters
{
    public void filterCreateTenantAcctPacket(ClientRequest req)
    {
        if (!(req.packet instanceof CreateAcctPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterCreateTenantAcctPacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        logic.handleCreateTenantAcct(req);
    }

    public void filterCreateLandlordAcctPacket(ClientRequest req)
    {
        if (!(req.packet instanceof CreateAcctPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterCreateLandlordAcctPacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        logic.handleCreateLandlordAcct(req);
    }

    public void filterCreateInvitePacket(ClientRequest req)
    {
        if (!(req.packet instanceof CreateInvitePacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterCreateInvitePacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        logic.handleCreateInvite(req);
    }

    public void filterAcceptInvitePacket(ClientRequest req)
    {
        if (!(req.packet instanceof AcceptInvitePacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterAcceptInvitePacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        logic.handleAcceptInvite(req);
    }

    public void filterGetInviteListPacket(ClientRequest req)
    {
        if (!(req.packet instanceof GetInviteListPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterGetInviteListPacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        logic.handleGetInviteList(req);
    }

    public void filterGetAcctLeasePacket(ClientRequest req)
    {
        if (!(req.packet instanceof GetAcctLeasePacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterGetAcctLeasePacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        logic.handleGetAcctLeasePacket(req);
    }

    public void filterGetAcctPropertyPacket(ClientRequest req)
    {
        if (!(req.packet instanceof GetAcctPropertyPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterGetPropertyPacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        //Validate user is a landlord
        PreparedStatement userQ = null;
        Boolean isUserLandlord = null;
        try
        {
            userQ = db.compileQuery("""
                    SELECT isLandlord
                    FROM Users
                    WHERE loginAuthToken = ?
                    """);

            userQ.setString(1, req.packet.token);

            ResultSet userR = userQ.executeQuery();

            if (userR.next())
            {
                isUserLandlord = userR.getBoolean("isLandlord");
            }
            else
            {
                //No entry for this user somehow
                System.out.println("ERROR: User passed login check but had bad token in landlord query somehow");
                req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
                req.sendResponse();

                return;
            }
        }
        catch (SQLException e)
        {
            //No entry for this user somehow
            System.out.println("ERROR: SQLException during filterGetPropertyPacket: " + e.toString());
            req.setUnknownErrResponse();
            req.sendResponse();

            return;
        }
        finally
        {
            db.closeConnection(userQ);
        }

        if (isUserLandlord == null || !isUserLandlord)
        {
            GetAcctPropertyResponse resp = new GetAcctPropertyResponse();
            resp.STATUS = GetAcctPropertyResponse.GetAcctPropertyStatus.ERR_NOT_LANDLORD;
            req.setResponse(resp);
            req.sendResponse();

            return;
        }

        //All checks passed, let it through

        logic.handleGetAcctPropertyPacket(req);
    }

    public void filterGetInviteInfoPacket(ClientRequest req)
    {
        if (!(req.packet instanceof GetInviteInfoPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterGetInviteInfoPacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        logic.handleGetInviteInfo(req);
    }
}
