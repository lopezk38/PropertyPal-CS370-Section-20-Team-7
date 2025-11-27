package com.propertypal.filters;

import com.propertypal.ClientRequest;
import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.enums.*;

public class NavigationFilters extends BaseFilters
{
    public void filterGetRolePacket(ClientRequest req)
    {
        if (!(req.packet instanceof GetRolePacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterGetRolePacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        //All checks passed, forward packet to next layer
        logic.handleGetRolePacket(req);
    }
}
