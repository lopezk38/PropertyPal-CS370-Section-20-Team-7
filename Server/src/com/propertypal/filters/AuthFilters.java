package com.propertypal.filters;

import com.propertypal.ClientRequest;
import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.enums.*;

public class AuthFilters extends BaseFilters
{
    public int enforceLoggedIn(ClientRequest req)
    {
        //Check DB for token, validate
        //TODO

        //If approved, allow packet to proceed
        return BaseResponseEnum.SUCCESS;

        //If rejected, respond with rejection and return with why
        //UNCOMMENT BELOW AFTER LOGIC ABOVE IS IMPLEMENTED
        //BaseResponse resp = new BaseResponse();
        //resp.STATUS = BaseResponseEnum.ERR_BAD_TOKEN;
        //req.setResponse(resp);
        //req.sendResponse();
        //return resp.STATUS;
    }

    public void filterLoginPacket(ClientRequest req)
    {
        if (!(req.packet instanceof LoginPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterLoginPacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //No restrictions for this type
        //Forward request
        logic.handleLogin(req);

        return;
    }

    public void filterLogoutPacket(ClientRequest req)
    {
        int loggedInCheckResult = enforceLoggedIn(req);
        if (loggedInCheckResult == BaseResponseEnum.ERR_BAD_TOKEN){ return; }

        //TODO logout path
    }
}
