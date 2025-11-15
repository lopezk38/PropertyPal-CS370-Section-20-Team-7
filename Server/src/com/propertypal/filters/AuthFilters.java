package com.propertypal.filters;

import com.propertypal.ClientRequest;
import com.propertypal.CoreLogic;
import com.propertypal.DbWrapper;
import com.propertypal.network.responses.*;

public class AuthFilters extends BaseFilters
{
    public int enforceLoggedIn(ClientRequest req)
    {
        //Check DB for token, validate

        //If approved, forward packet to core logic

        //If rejected, respond with rejection and return with why
        BaseResponse resp = new BaseResponse();
        resp.STATUS = BaseResponseEnum.ERR_BAD_TOKEN;
        req.setResponse(resp);
        req.sendResponse();
        return resp.STATUS;
    }

    public void filterLoginPacket(ClientRequest req)
    {
        //No restrictions for this type
        //Forward request
        CoreLogic logic = CoreLogic.getInstance();
        logic.handleLogin(req);

        return;
    }

    public void filterLogoutPacket(ClientRequest req)
    {
        int loggedInCheckResult = enforceLoggedIn(req);
        if (loggedInCheckResult == BaseResponseEnum.ERR_BAD_TOKEN){ return; }

        //TODO logout path
    }

    public void filterCreateTenantAcctPacket(ClientRequest req)
    {

        //TODO logout path
    }
}
