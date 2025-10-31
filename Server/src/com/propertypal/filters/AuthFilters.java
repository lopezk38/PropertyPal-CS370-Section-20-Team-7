package com.propertypal.filters;

import com.propertypal.ClientRequest;
import com.propertypal.CoreLogic;
import com.propertypal.DbWrapper;
import com.propertypal.network.responses.*;

public class AuthFilters
{
    private CoreLogic logic = CoreLogic.getInstance();
    private DbWrapper db = DbWrapper.getInstance();

    public BaseResponseEnum enforceLoggedIn(ClientRequest req)
    {
        //Check DB for token, validate

        //If approved, forward packet to core logic

        //If rejected, respond with rejection and return with why
        BaseResponse resp = new BaseResponse();
        resp.STATUS = BaseResponse.ERR_BAD_TOKEN;
        req.source.sendResp(resp.toJson());
        return resp.STATUS;
    }

    public LoginResponse.LoginStatus filterLoginPacket(ClientRequest req)
    {
        //No restrictions for this type
        //Forward request
        CoreLogic.HandleLogin(req);

        return LoginResponse.SUCCESS;
    }

    public LogoutResponse.LogoutStatus filterLogoutPacket(ClientRequest req)
    {
        BaseResponseEnum loginCheckResult = enforceLoggedIn(req);
        if (logoutCheckResult == BaseResponse.ERR_BAD_TOKEN) { return loginCheckResult; }
    }
}
