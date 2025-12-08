package com.propertypal.server.filters;

import com.propertypal.server.ClientRequest;
import com.propertypal.server.CoreLogic;
import com.propertypal.server.DbWrapper;
import com.propertypal.server.SecurityFilter;
import com.propertypal.server.LogicBlocks.AccountLogic;

import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.enums.*;

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
}
