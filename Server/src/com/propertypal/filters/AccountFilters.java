package com.propertypal.filters;

import com.propertypal.ClientRequest;
import com.propertypal.CoreLogic;
import com.propertypal.DbWrapper;
import com.propertypal.LogicBlocks.AccountLogic;
import com.propertypal.network.responses.*;

public class AccountFilters extends BaseFilters
{
    public void filterCreateTenantAcctPacket(ClientRequest req)
    {
        CoreLogic logic = CoreLogic.getInstance();
        logic.handleCreateTenantAcct(req);
    }

    public void filterCreateInvitePacket(ClientRequest req)
    {
        CoreLogic logic = CoreLogic.getInstance();
        logic.handleCreateInvite(req);
    }

    public void filterAcceptInvitePacket(ClientRequest req)
    {
        CoreLogic logic = CoreLogic.getInstance();
        logic.handleAcceptInvite(req);
    }
}
