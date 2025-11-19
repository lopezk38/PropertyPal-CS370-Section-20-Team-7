package com.propertypal.filters;

import com.propertypal.ClientRequest;
import com.propertypal.CoreLogic;
import com.propertypal.DbWrapper;
import com.propertypal.SecurityFilter;
import com.propertypal.LogicBlocks.AccountLogic;
import com.propertypal.network.responses.*;
import com.propertypal.network.packets.*;
import com.propertypal.network.enums.*;

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

    public void filterCreateInvitePacket(ClientRequest req)
    {
        logic.handleCreateInvite(req);
    }

    public void filterAcceptInvitePacket(ClientRequest req)
    {
        logic.handleAcceptInvite(req);
    }
}
