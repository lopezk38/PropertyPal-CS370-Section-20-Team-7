package com.propertypal.filters;

import com.propertypal.ClientRequest;
import com.propertypal.CoreLogic;
import com.propertypal.DbWrapper;
import com.propertypal.LogicBlocks.AccountLogic;
import com.propertypal.network.responses.*;

public class AccountFilters extends BaseFilters
{
    public void filterCreateAcctPacket(ClientRequest req)
    {
        CoreLogic logic = CoreLogic.getInstance();
        logic.handleCreateAcct(req);
    }

    public void filterCreateInvitePacket(ClientRequest req)
    {

    }
}
