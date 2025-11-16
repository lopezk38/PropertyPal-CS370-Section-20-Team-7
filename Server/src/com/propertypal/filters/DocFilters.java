package com.propertypal.filters;

import com.propertypal.ClientRequest;
import com.propertypal.CoreLogic;
import com.propertypal.DbWrapper;
import com.propertypal.LogicBlocks.AccountLogic;
import com.propertypal.network.responses.*;

public class DocFilters extends BaseFilters
{

    public void filterUploadDocPacket(ClientRequest req)
    {
        CoreLogic logic = CoreLogic.getInstance();
        logic.handleUploadDoc(req);
    }
}
